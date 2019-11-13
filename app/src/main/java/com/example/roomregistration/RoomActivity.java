package com.example.roomregistration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoomActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{
    public static final String BASE_URI = "http://anbo-roomreservationv3.azurewebsites.net/api/reservations/room/";
    public static final String ROOM = "ROOM";
    private Room room;
    private GestureDetector gestureDetector;
    private boolean delete = false;
    private FloatingActionButton delfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        gestureDetector = new GestureDetector(this, this);

        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_compass);

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra(ROOM);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        delfab = (FloatingActionButton)findViewById(R.id.delfab);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            fab.hide();
            delfab.hide();
        }

        TextView roomLabelTxtView = findViewById(R.id.txtRoomLabel);
        roomLabelTxtView.setText(room.toString());
        getDataUsingOkHttpEnqueue();
    }
    public void setDelete(View v) {
        delete = !delete;
        if (delete) {
            delfab.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_delete));
        }
        else {
            delfab.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_delete));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(RoomActivity.this, RoomOverviewActivity.class);
                RoomActivity.this.startActivity(i);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void getDataUsingOkHttpEnqueue() {
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(BASE_URI+room.getId());
        Request request = requestBuilder.build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonString = response.body().string();
                    runOnUiThread(new Runnable() {
                        // https://stackoverflow.com/questions/33418232/okhttp-update-ui-from-enqueue-callback
                        @Override
                        public void run() {
                            populateList(jsonString);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //ToDO Exception?
                            //TextView messageView = findViewById(R.id.main_message_textview);
                            //messageView.setText(BASE_URI + "\n" + response.code() + " " + response.message());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TextView messageView = findViewById(R.id.main_message_textview);
                        //messageView.setText(ex.getMessage());
                    }
                });
            }
        });
    }
    private void populateList(String jsonString) {
        Gson gson = new GsonBuilder().create();
        Log.d("ROA", jsonString);
        Reservation[] tempReservations = gson.fromJson(jsonString, Reservation[].class);
        final ArrayList<Reservation> reservations = new ArrayList<Reservation>(Arrays.asList(tempReservations));
        Collections.sort(reservations, new Comparator<Reservation>() {
            public int compare(Reservation o1, Reservation o2) {
                return o1.compareTo(o2);
            }
        });
        ListView listView = findViewById(R.id.listviewReservations);
        final ArrayAdapter<Reservation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reservations);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RoomActivity.this, "Reservation clicked: "+parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                Reservation clickedRes = (Reservation) parent.getItemAtPosition(position);
                if (delete) {
                    if (clickedRes.getuserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Toast.makeText(RoomActivity.this, "UID matched", Toast.LENGTH_SHORT).show();
                        deleteReservation(clickedRes);
                        reservations.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(RoomActivity.this, "UID didn't match", Toast.LENGTH_SHORT).show();
                        Toast.makeText(RoomActivity.this, "res: " + clickedRes.getuserId() + "\nid: " + FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    public void addReservation(View view) {
        Intent intent = new Intent(this, AddReservationActivity.class);
        intent.putExtra(ROOM, room);
        startActivity(intent);
    }

    public void deleteReservation(Reservation reservation) {
        final String url = "http://anbo-roomreservationv3.azurewebsites.net/api/reservations/" + reservation.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).delete().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException ex) {
                Toast.makeText(RoomActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                if (response.isSuccessful()) {
                            //Toast.makeText(RoomActivity.this, "Reservation deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            //Toast.makeText(RoomActivity.this, "Deletion failed", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    // region implements gestureDetector
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        this.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        boolean leftSwipe = event1.getX() < event2.getX() + 200 && Math.abs(event1.getY()-event2.getY()) < 200;
        if (leftSwipe) {
            Intent i = new Intent(RoomActivity.this, MainActivity.class);
            //i.putStringArrayListExtra("logArray", log);
            RoomActivity.this.startActivity(i);
        }
        return true; // done
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }
    // endregion implements gestureDetector
}

package com.example.roomregistration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        gestureDetector = new GestureDetector(this, this);

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra(ROOM);

        TextView roomLabelTxtView = findViewById(R.id.txtRoomLabel);
        roomLabelTxtView.setText(room.toString());
        getDataUsingOkHttpEnqueue();
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
        final Reservation[] reservations = gson.fromJson(jsonString, Reservation[].class);
        ListView listView = findViewById(R.id.listviewReservations);
        ArrayAdapter<Reservation> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reservations);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RoomActivity.this, "Reservation clicked: "+parent.getItemAtPosition(position), Toast.LENGTH_SHORT)
                        .show();
                /*
                Intent intent = new Intent(getBaseContext(), RoomActivity.class);
                Room room = (Room) parent.getItemAtPosition(position);
                intent.putExtra(RoomActivity.ROOM, room);
                startActivity(intent);
                */
            }
        });
    }
    // region implements gestureDetector
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        boolean rightSwipe = event1.getX() > event2.getX();
        if (rightSwipe) {
            return true;
        }
        else {
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

package com.example.roomregistration

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_add_reservation.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddReservationActivity : AppCompatActivity() {
    private var calendar: Calendar = Calendar.getInstance();
    private var fromHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
    private var toHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
    private var fromMin: Int = calendar.get(Calendar.MINUTE)
    private var toMin: Int = calendar.get(Calendar.MINUTE)
    val BASE_URI = "http://anbo-roomreservationv3.azurewebsites.net/api/reservations/room/"
    private lateinit var room: Room;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reservation)
        setSupportActionBar(my_toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_compass)

        val intent = intent
        room = intent.getSerializableExtra(RoomActivity.ROOM) as Room

        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        in_date.text = sdf.format(calendar.time)
        in_fromtime.text = fromHour.toString().padStart(2,'0') + " : " + fromMin.toString().padStart(2,'0');
        in_totime.text = toHour.toString().padStart(2,'0') + " : " + toMin.toString().padStart(2,'0');

        fab.setOnClickListener { view ->
            saveReservation(view);
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home ->{
            val i = Intent(this, RoomOverviewActivity::class.java)
            this.startActivity(i)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun datePicker(view: View) {


        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "yyyy-MM-dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            in_date.text = sdf.format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        dpd.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun fromTimePicker(view: View) {


        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

            fromHour = h;
            fromMin = m;
            in_fromtime.text = h.toString().padStart(2,'0') + " : " + m.toString().padStart(2,'0');

        }),fromHour,fromMin,true)

        tpd.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun toTimePicker(view: View) {


        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

            toHour = h;
            toMin = m;
            in_totime.text = h.toString().padStart(2,'0') + " : " + m.toString().padStart(2,'0');

        }),toHour,toMin,true)

        tpd.show()
    }

    public fun saveReservation(view: View) {
        val fromtime = getTime(fromHour, fromMin);
        val toTime = getTime(toHour, toMin);
        val purpose = in_purpose.text.toString()

        if (purpose.length > 2) {
            if (fromtime < toTime) {
                checkReservationSameTime(fromtime, toTime, purpose)
            }
            else Toast.makeText(this, "Reservation can't end before it begins", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Purpose has to be atleast 2 characters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTime(hour: Int, min: Int): Long {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        return calendar.timeInMillis / 1000;
    }

    private fun checkReservationSameTime(fromTime: Long, toTime: Long, purpose: String) {
        val client = OkHttpClient()
        val requestBuilder = Request.Builder()
        requestBuilder.url(BASE_URI + room.getId() + "/" + fromTime + "/" + toTime)
        val request = requestBuilder.build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonString = response.body!!.string()
                    if (populateList(jsonString)) {
                        postReservation(fromTime, toTime, purpose)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(baseContext, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, ex: IOException) {
                runOnUiThread {
                    Toast.makeText(baseContext, "No response from server", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun populateList(jsonString: String): Boolean {
        val gson = GsonBuilder().create()
        Log.d("ROA", jsonString)
        if (gson.fromJson(jsonString, Array<Reservation>::class.java).size >= 1) {
            Toast.makeText(this, "Room is reserved at selected time", Toast.LENGTH_SHORT).show()
        }
        return gson.fromJson(jsonString, Array<Reservation>::class.java).size < 1
    }
    private fun postReservation(fromTime: Long, toTime: Long, purpose: String) {
        val url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations";

        val reservation = Reservation(0, fromTime, toTime, FirebaseAuth.getInstance().currentUser?.uid, purpose, room.id)
        val jsonRes: String = Gson().toJson(reservation)

        val client = OkHttpClient()

        val requestBody = jsonRes.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("TAG", response.toString())
                if(!response.isSuccessful){
                    runOnUiThread {
                        Toast.makeText(baseContext, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    runOnUiThread {
                        val intent = Intent(this@AddReservationActivity, RoomActivity::class.java)
                        intent.putExtra(RoomActivity.ROOM, room)
                        startActivity(intent)
                    }
                }
            }
        })
        setResult(Activity.RESULT_OK)
        finish()
    }
}

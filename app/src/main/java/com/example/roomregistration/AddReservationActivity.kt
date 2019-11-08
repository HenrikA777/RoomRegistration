package com.example.roomregistration

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_reservation.*
import java.text.SimpleDateFormat
import java.util.*

class AddReservationActivity : AppCompatActivity() {
    private var calendar: Calendar = Calendar.getInstance();
    private var fromHour: Int = calendar.get(Calendar.HOUR)
    private var toHour: Int = calendar.get(Calendar.HOUR)
    private var fromMin: Int = calendar.get(Calendar.MINUTE)
    private var toMin: Int = calendar.get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reservation)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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

}

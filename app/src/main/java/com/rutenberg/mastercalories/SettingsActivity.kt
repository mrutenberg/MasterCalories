package com.rutenberg.mastercalories

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.MenuItem
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val timePicker = findViewById<TimePicker>(R.id.time_picker)
        val saveButton = findViewById<Button>(R.id.btn_save_reset_time)
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this))

        val prefs = getSharedPreferences(
            AdditionCalculatorWidget.getSharedPrefsName(),
            Context.MODE_PRIVATE
        )
        val savedHour = prefs.getInt(AdditionCalculatorWidget.getResetHourKey(), 2)
        val savedMinute = prefs.getInt(AdditionCalculatorWidget.getResetMinuteKey(), 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = savedHour
            timePicker.minute = savedMinute
        } else {
            timePicker.currentHour = savedHour
            timePicker.currentMinute = savedMinute
        }

        saveButton.setOnClickListener {
            val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.hour
            } else {
                timePicker.currentHour ?: 0
            }
            val minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.minute
            } else {
                timePicker.currentMinute ?: 0
            }
            prefs.edit()
                .putInt(AdditionCalculatorWidget.getResetHourKey(), hour)
                .putInt(AdditionCalculatorWidget.getResetMinuteKey(), minute)
                .apply()
            MidnightResetReceiver.schedule(applicationContext)
            Toast.makeText(this, R.string.reset_time_saved, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

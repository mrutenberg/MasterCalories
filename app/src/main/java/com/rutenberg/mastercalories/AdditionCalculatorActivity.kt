package com.rutenberg.mastercalories

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.Calendar
import android.view.View

class AdditionCalculatorActivity : AppCompatActivity() {

    private lateinit var amountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addition_calculator)

        amountTextView = findViewById(R.id.tv_amount)
        updateAmount(getAmount())

        scheduleMidnightReset()
    }

    private fun getPrefs(): SharedPreferences {
        return getSharedPreferences(AdditionCalculatorWidget.getSharedPrefsName(), Context.MODE_PRIVATE)
    }

    private fun getAmount(): Int {
        val prefs = getPrefs()
        return prefs.getInt(AdditionCalculatorWidget.getAmountKey(), 0)
    }

    private fun setAmount(amount: Int) {
        val prefs = getPrefs()
        prefs.edit().putInt(AdditionCalculatorWidget.getAmountKey(), amount).apply()
        updateWidgets()
    }

    private fun updateWidgets() {
        val intent = Intent(this, AdditionCalculatorWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(ComponentName(application, AdditionCalculatorWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    fun onAdd25Clicked(view: View) {
        val amount = getAmount() + 25
        setAmount(amount)
        updateAmount(amount)
    }

    fun onAdd100Clicked(view: View) {
        val amount = getAmount() + 100
        setAmount(amount)
        updateAmount(amount)
    }

    fun onRemove25Clicked(view: View) {
        val amount = getAmount() - 25
        setAmount(amount)
        updateAmount(amount)
    }

    private fun updateAmount(amount: Int) {
        amountTextView.text = amount.toString()
    }

    override fun onResume() {
        super.onResume()
        updateAmount(getAmount())
    }

    private fun scheduleMidnightReset() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MidnightResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1) // Schedule for the next day
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

}

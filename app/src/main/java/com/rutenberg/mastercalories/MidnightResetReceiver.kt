package com.rutenberg.mastercalories

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.Calendar


class MidnightResetReceiver : BroadcastReceiver() {
    companion object {
        fun schedule(context: Context) {
            val prefs = context.getSharedPreferences(
                AdditionCalculatorWidget.getSharedPrefsName(),
                Context.MODE_PRIVATE
            )
            val resetHour = prefs.getInt(AdditionCalculatorWidget.getResetHourKey(), 2)
            val resetMinute = prefs.getInt(AdditionCalculatorWidget.getResetMinuteKey(), 0)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MidnightResetReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val calendar = Calendar.getInstance().apply {
                val now = System.currentTimeMillis()
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, resetHour)
                set(Calendar.MINUTE, resetMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= now) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            alarmManager.cancel(pendingIntent)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences(AdditionCalculatorWidget.getSharedPrefsName(), Context.MODE_PRIVATE)
        prefs.edit().putInt(AdditionCalculatorWidget.getAmountKey(), 0).apply()

        // Update the widgets
        val updateIntent = Intent(context, AdditionCalculatorWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, AdditionCalculatorWidget::class.java))
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(updateIntent)

        schedule(context.applicationContext)
    }
}

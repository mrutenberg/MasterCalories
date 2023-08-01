package com.rutenberg.mastercalories

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.content.ComponentName


class MidnightResetReceiver : BroadcastReceiver() {
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
    }
}

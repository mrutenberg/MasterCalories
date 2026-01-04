package com.rutenberg.mastercalories

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import android.app.PendingIntent
import android.os.Build

class AdditionCalculatorWidget : AppWidgetProvider() {

    companion object {
        private const val ADD_25_ACTION = "com.rutenberg.ADD_25"
        private const val ADD_100_ACTION = "com.rutenberg.ADD_100"
        private const val REMOVE_25_ACTION = "com.rutenberg.REMOVE_25"
        private const val PREFS_NAME = "com.rutenberg.mastercalories.AdditionCalculatorWidget"
        private const val KEY_AMOUNT = "amount"
        private const val KEY_RESET_HOUR = "reset_hour"
        private const val KEY_RESET_MINUTE = "reset_minute"

        fun getSharedPrefsName(): String {
            return PREFS_NAME
        }

        fun getAmountKey(): String {
            return KEY_AMOUNT
        }

        fun getResetHourKey(): String {
            return KEY_RESET_HOUR
        }

        fun getResetMinuteKey(): String {
            return KEY_RESET_MINUTE
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        MidnightResetReceiver.schedule(context.applicationContext)
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.addition_calculator_widget)
        val amount = getPrefs(context).getInt(KEY_AMOUNT, 0)
        remoteViews.setTextViewText(R.id.tv_amount, amount.toString())

        // Set the onClickListener for each button
        setOnClickListeners(context, remoteViews, appWidgetId)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun setOnClickListeners(context: Context, remoteViews: RemoteViews, appWidgetId: Int) {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        // Add 25 button
        val add25Intent = Intent(context, AdditionCalculatorWidget::class.java)
        add25Intent.action = ADD_25_ACTION
        add25Intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val add25PendingIntent = PendingIntent.getBroadcast(context, 0, add25Intent, flags)
        remoteViews.setOnClickPendingIntent(R.id.btn_add_25, add25PendingIntent)

        // Add 100 button
        val add100Intent = Intent(context, AdditionCalculatorWidget::class.java)
        add100Intent.action = ADD_100_ACTION
        add100Intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val add100PendingIntent = PendingIntent.getBroadcast(context, 1, add100Intent, flags)
        remoteViews.setOnClickPendingIntent(R.id.btn_add_100, add100PendingIntent)

        // Remove 25 button
        val remove25Intent = Intent(context, AdditionCalculatorWidget::class.java)
        remove25Intent.action = REMOVE_25_ACTION
        remove25Intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val remove25PendingIntent = PendingIntent.getBroadcast(context, 2, remove25Intent, flags)
        remoteViews.setOnClickPendingIntent(R.id.btn_subtract_25, remove25PendingIntent)

        // Add intent for launching AdditionCalculatorActivity
        val launchAppIntent = Intent(context, AdditionCalculatorActivity::class.java)
        val launchAppPendingIntent = PendingIntent.getActivity(
            context,
            3,
            launchAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.tv_amount, launchAppPendingIntent)

    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        MidnightResetReceiver.schedule(context.applicationContext)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return
        }

        val prefs = getPrefs(context)
        val currentAmount = prefs.getInt(KEY_AMOUNT, 0)
        val newAmount = when (intent.action) {
            ADD_25_ACTION -> currentAmount + 25
            ADD_100_ACTION -> currentAmount + 100
            REMOVE_25_ACTION -> currentAmount - 25
            else -> currentAmount
        }

        // Save the new amount in shared preferences
        prefs.edit().putInt(KEY_AMOUNT, newAmount).apply()

        // Update the widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateWidget(context, appWidgetManager, appWidgetId)
    }
}

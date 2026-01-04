package com.rutenberg.mastercalories

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.view.View
import android.view.Menu
import android.view.MenuItem

class AdditionCalculatorActivity : AppCompatActivity() {

    private lateinit var amountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addition_calculator)

        amountTextView = findViewById(R.id.tv_amount)
        updateAmount(getAmount())

        MidnightResetReceiver.schedule(applicationContext)
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

    fun onRemove100Clicked(view: View) {
        val amount = getAmount() - 100
        setAmount(amount)
        updateAmount(amount)
    }

    fun onResetClicked(view: View) {
        val amount = 0
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_addition_calculator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

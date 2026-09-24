package com.pratikbhosale.daybook

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.pratikbhosale.daybook.widget.DaybookWidgetReceiver

class PinWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val myProvider = ComponentName(context, DaybookWidgetReceiver::class.java)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(myProvider, null, null)
            Toast.makeText(context, "Requested to pin widget", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Pinning not supported on this launcher", Toast.LENGTH_SHORT).show()
        }
    }
}

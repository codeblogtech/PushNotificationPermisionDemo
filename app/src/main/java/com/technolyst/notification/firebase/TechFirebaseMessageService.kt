package com.technolyst.notification.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.technolyst.notification.MainActivity
import com.technolyst.notification.R
import com.technolyst.notification.dataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TechFirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //Log incoming message
        Log.v("CloudMessage", "From ${message.from}")

        //Log Data Payload
        if (message.data.isNotEmpty()) {
            Log.v("CloudMessage", "Message Data ${message.data}")
        }

        //Check if message contains a notification payload

        message.data.let {
            Log.v("CloudMessage", "Message Notification Body ${it["body"]}")
            showNotification(it)
        }

        if (message.notification != null) {

            Log.v("CloudMessage", "Notification ${message.notification}")
            Log.v("CloudMessage", "Notification Title ${message.notification!!.title}")
            Log.v("CloudMessage", "Notification Body ${message.notification!!.body}")

        }

    }

    private fun showNotification(data: Map<String, String>) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        }

        intent.putExtra("title", data["title"])
        intent.putExtra("body", data["body"])
        var requestCode = System.currentTimeMillis().toInt()

        var pendingIntent: PendingIntent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or FLAG_MUTABLE
            )
        } else {
            pendingIntent = PendingIntent.getActivity(
                this,
                requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
        }


        var builder = NotificationCompat.Builder(this, "Global").setAutoCancel(true)
            .setContentTitle(data["title"])
            .setContentText(data["body"]).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data["body"]))
            .setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_notification)

        with(NotificationManagerCompat.from(this)) {
            notify(requestCode, builder.build())
        }

    }




    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch {
            saveGCMToken(token)
        }
    }

    //Save GCM Token DataStore Preference
    // you can used to send it on your Server.
    private suspend fun saveGCMToken(token: String) {
        val gckTokenKey = stringPreferencesKey("gcm_token")
        baseContext.dataStore.edit { pref ->
            pref[gckTokenKey] = token
        }
    }
}
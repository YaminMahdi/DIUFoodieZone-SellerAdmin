package com.diu.mlab.foodie.admin.presentation.main.seller

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.diu.mlab.foodie.admin.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {
    private var id = 0


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(chId, chNm, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.WHITE
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val intent = Intent(this, SellerMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        // Check if message contains a notification payload.
        val title = remoteMessage.data["title"]?:""
        val body = remoteMessage.data["body"]?:"null"

        Log.d(TAG, "Message Notification Body: $body")
        val notificationBuilder= NotificationCompat.Builder(applicationContext, chId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_food)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setContentIntent(pendingIntent)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        )
            NotificationManagerCompat.from(this).notify(id++,notificationBuilder.build())
        else
            Log.d(TAG, "onMessageReceived: PERMISSION_denied")

//        remoteMessage.notification?.let {
//
//        }

    }

    companion object {
        private const val TAG = "FirebaseNotificationService"
        private const val chId = "alert_notification"
        private const val chNm = "com.mlab"

    }
}
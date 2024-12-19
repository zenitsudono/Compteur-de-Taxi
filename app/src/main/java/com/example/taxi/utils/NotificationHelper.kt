package com.example.taxi.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.taxi.R


class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "fare_channel",
                "Ride Fare Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendFareNotification(fare: String, distance: String, time: String) {
        val notification = NotificationCompat.Builder(context, "fare_channel")
            .setContentTitle("Ride Completed")
            .setContentText("Fare:$fare DH Distance:$distance km Time: $time min")
            .setSmallIcon(R.drawable.ic_car)
            .build()

        notificationManager.notify(1, notification)
    }
}

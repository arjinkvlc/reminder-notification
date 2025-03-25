package com.example.reminderpopup.reminder.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.reminderpopup.MainActivity
import com.example.reminderpopup.R
import com.example.reminderpopup.reminder.data.ReminderData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ReminderFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            try {
                val reminder = ReminderData(
                    title = data["title"] ?: "Hatırlatıcı",
                    message = data["message"] ?: "",
                    day = data["day"] ?: "FRIDAY",
                    startHour = data["startHour"]?.toIntOrNull() ?: 9,
                    endHour = data["endHour"]?.toIntOrNull() ?: 10,
                    repeatType = data["repeatType"] ?: "WEEKLY"
                )
                showNotification(reminder)
            } catch (e: Exception) {
                Log.e("FCM", "Reminder parse hatası: ${e.message}")
            }
        }
    }

    private fun showNotification(reminder: ReminderData) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("title", reminder.title)
            putExtra("message", reminder.message)
            putExtra("day", reminder.day)
            putExtra("startHour", reminder.startHour)
            putExtra("endHour", reminder.endHour)
            putExtra("repeatType", reminder.repeatType)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "reminder_channel")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(reminder.title)
            .setContentText(reminder.message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(1001, builder.build())
    }
}

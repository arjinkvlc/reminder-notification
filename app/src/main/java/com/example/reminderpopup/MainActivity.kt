package com.example.reminderpopup

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.reminderpopup.reminder.data.ReminderData
import com.example.reminderpopup.reminder.presentation.ReminderScreen
import com.example.reminderpopup.ui.theme.ReminderPopupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestCalendarPermission(this)

        val reminder = intent?.let {
            ReminderData(
                title = it.getStringExtra("title") ?: return@let null,
                message = it.getStringExtra("message") ?: return@let null,
                day = it.getStringExtra("day") ?: return@let null,
                startHour = it.getIntExtra("startHour", 9),
                endHour = it.getIntExtra("endHour", 10),
                repeatType = it.getStringExtra("repeatType") ?: "WEEKLY"
            )
        }

        setContent {
            ReminderPopupTheme {
                ReminderScreen(context = this, reminderFromIntent = reminder)
            }
        }
    }

    private fun requestCalendarPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR),
                1001
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}
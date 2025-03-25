package com.example.reminderpopup.reminder.util

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.reminderpopup.reminder.data.ReminderData
import java.util.Calendar
import java.util.TimeZone

object CalendarUtil {
    private fun getBestCalendarId(context: Context): Long? {
        val uri = CalendarContract.Calendars.CONTENT_URI
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.IS_PRIMARY,
        )

        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        var defaultCalendarId: Long? = null
        var firstAvailableCalendarId: Long? = null

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                val name = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.NAME))
                val isPrimary = it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.IS_PRIMARY))

                if (isPrimary == 1) defaultCalendarId = id
                if (firstAvailableCalendarId == null) firstAvailableCalendarId = id
            }
        }

        return defaultCalendarId ?: firstAvailableCalendarId
    }

    fun addReminderToCalendar(context: Context, reminderData: ReminderData) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val calendar = Calendar.getInstance()
        val dayOfWeek = when (reminderData.day.uppercase()) {
            "SUNDAY" -> Calendar.SUNDAY
            "MONDAY" -> Calendar.MONDAY
            "TUESDAY" -> Calendar.TUESDAY
            "WEDNESDAY" -> Calendar.WEDNESDAY
            "THURSDAY" -> Calendar.THURSDAY
            "FRIDAY" -> Calendar.FRIDAY
            "SATURDAY" -> Calendar.SATURDAY
            else -> Calendar.FRIDAY
        }

        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, reminderData.startHour)
        calendar.set(Calendar.MINUTE, 0)

        val startMillis = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, reminderData.endHour)
        val endMillis = calendar.timeInMillis

        val recurrence = when (reminderData.repeatType.uppercase()) {
            "WEEKLY" -> "FREQ=WEEKLY;BYDAY=${reminderData.day.take(2).uppercase()}"
            else -> null
        }

        val calendarId = getBestCalendarId(context) ?: return

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, reminderData.title)
            put(CalendarContract.Events.DESCRIPTION, reminderData.message)
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED)
            put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            recurrence?.let { put(CalendarContract.Events.RRULE, it) }
        }

        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        if (uri != null) {
            Toast.makeText(context, "Hatırlatıcı başarıyla takvime eklendi.", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.reminderpopup.reminder.data

import kotlinx.serialization.Serializable

@Serializable
data class ReminderData(
    val title: String,
    val message: String,
    val day: String, // "FRIDAY", "MONDAY" gibi string değer alacak
    val startHour: Int, // 24 saat formatında saat
    val endHour: Int, // 24 saat formatında saat
    val repeatType: String // "WEEKLY" veya "ONCE"
)

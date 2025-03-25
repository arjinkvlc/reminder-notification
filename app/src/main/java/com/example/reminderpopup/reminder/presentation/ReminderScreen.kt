package com.example.reminderpopup.reminder.presentation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.reminderpopup.reminder.data.ReminderData
import com.example.reminderpopup.reminder.util.CalendarUtil

@Composable
fun ReminderScreen(
    context: Context,
    reminderFromIntent: ReminderData? = null
) {
    var showDialog by remember { mutableStateOf(reminderFromIntent != null) }

    if (showDialog && reminderFromIntent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    CalendarUtil.addReminderToCalendar(context, reminderFromIntent)
                    showDialog = false
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hayır")
                }
            },
            title = { Text("Takvime Hatırlatıcı Ekle") },
            text = { Text(reminderFromIntent.message) }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Calendar Reminder App", modifier = Modifier.fillMaxWidth(),style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        reminderFromIntent?.let {
            Text(text = "Hatırlatıcı: ${it.title}", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}



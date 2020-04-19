package com.example.repositories

import android.content.Context
import com.example.dao.ReminderDao
import com.example.models.ReminderRoomDatabase
import com.example.reminder.Reminder

public class ReminderRepository(context: Context) {
    private var reminderDao: ReminderDao?

    init {
        val reminderRoomDatabase = ReminderRoomDatabase.getDatabase(context)
        reminderDao = reminderRoomDatabase?.reminderDao()
    }

    suspend fun getAllReminders(): List<Reminder> {
        return reminderDao?.getAllReminders() ?: emptyList()
    }

    suspend fun insertReminder(reminder: Reminder) {
        reminderDao?.insertReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao?.deleteReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao?.updateReminder(reminder)
    }

}



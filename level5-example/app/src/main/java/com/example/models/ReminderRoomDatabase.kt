package com.example.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dao.ReminderDao
import com.example.reminder.Reminder

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class ReminderRoomDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        private const val DATABASE_NAME = "REMINDER_DATABASE"

        @Volatile
        private var reminderRDInstance: ReminderRoomDatabase? = null

        fun getDatabase(context: Context): ReminderRoomDatabase? {
            if (reminderRDInstance == null) {
                synchronized(ReminderRoomDatabase::class.java) {
                    if (reminderRDInstance == null) {
                        reminderRDInstance = Room.databaseBuilder(
                                context.applicationContext,
                                ReminderRoomDatabase::class.java, DATABASE_NAME
                            )
                            .build()
                    }
                }
            }
            return reminderRDInstance
        }
    }

}

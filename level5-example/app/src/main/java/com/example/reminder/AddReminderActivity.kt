package com.example.reminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_add_reminder.*
import kotlinx.android.synthetic.main.content_add_reminder.*

const val EXTRA_REMINDER = "EXTRA_REMINDER"

class AddReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)
        setSupportActionBar(toolbar)

        initViews()
    }

    private fun initViews() {
        // add the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener { onSaveClick() }
    }

    /**
     * Add reminder if reminder isn't empty
     */
    private fun onSaveClick() {
        val reminderString = etAddReminder.text.toString()
        val resultIntent = Intent()

        if (reminderString.isNotBlank()) {
            val reminder = Reminder(reminderString)
            resultIntent.putExtra(EXTRA_REMINDER, reminder)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else Toast.makeText(this, "Reminder cannot be empty.", Toast.LENGTH_SHORT).show()

    }

}

package com.example.reminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repositories.ReminderRepository

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_add_reminder.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ADD_REMINDER_REQUEST_CODE = 100

class ReminderActivity : AppCompatActivity() {
    private val reminders = arrayListOf<Reminder>()
    private val reminderAdapter = ReminderAdapter(reminders)
    private lateinit var reminderRepository: ReminderRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)
        reminderRepository = ReminderRepository(this)
        initViews()
        fab.setOnClickListener { startAddActivity() }
    }

    private fun initViews() {
        // Initialize the recycler view with a linear layout manager, adapter
        rvReminders.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvReminders.adapter = reminderAdapter

        rvReminders.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        createItemTouchHelper().attachToRecyclerView(rvReminders)
        getRemindersFromDB()
    }

    private fun getRemindersFromDB() {
        CoroutineScope(Dispatchers.Main).launch {
            val reminders = withContext(Dispatchers.IO) {
                reminderRepository.getAllReminders()
            }
            this@ReminderActivity.reminders.clear()
            this@ReminderActivity.reminders.addAll(reminders)
            reminderAdapter.notifyDataSetChanged()
        }
    }

    private fun startAddActivity() {
        val intent = Intent(this, AddReminderActivity::class.java)
        startActivityForResult(intent, ADD_REMINDER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_REMINDER_REQUEST_CODE -> {
                    val reminder = data?.getParcelableExtra<Reminder>(EXTRA_REMINDER)
                    reminder?.let {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                reminderRepository.insertReminder(reminder)
                            }
                            getRemindersFromDB()
                        }
                    } ?: run {
                        Log.e("addReminder", "Something went wrong while adding reminder")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Create a touch helper to recognize when a user swipes an item from a recycler view.
     * An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
     * and uses callbacks to signal when a user is performing these actions.
     */
    private fun createItemTouchHelper(): ItemTouchHelper {


        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Delete reminder once user swipes left
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val reminderToDelete = reminders[position]

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        reminderRepository.deleteReminder(reminderToDelete)
                    }
                }

                getRemindersFromDB()
            }

        }
        return ItemTouchHelper(callback)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

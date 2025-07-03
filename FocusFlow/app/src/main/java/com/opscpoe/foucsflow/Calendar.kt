package com.opscpoe.foucsflow

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var habitListView: ListView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var selectedDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Initialize views and Firebase
        calendarView = findViewById(R.id.calendarView)
        habitListView = findViewById(R.id.habitListView)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Load habits initially
        loadHabitsToListView()

        // Calendar date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis

            // Show habits only for the selected date
            showHabitsForDate(selectedDate)
        }
    }

    // Load all habits into the ListView initially
    private fun loadHabitsToListView() {
        val userId = auth.currentUser?.uid

        db.collection("habits")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val habitInfoList = documents.map { document ->
                    val name = document.getString("name") ?: "Unnamed Habit"
                    val startDate = document.getString("startDate") ?: "Unknown Start Date"
                    val endDate = document.getString("endDate") ?: "Unknown End Date"
                    formatHabitDisplay(name, startDate, endDate)
                }
                updateHabitListView(habitInfoList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load habits: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Format the habit display string to inlcude the start and end date
    private fun formatHabitDisplay(name: String, startDate: String, endDate: String): String {
        return "$name - $startDate till $endDate"
    }

    // Update ListView with the  formatted habit info
    private fun updateHabitListView(habitInfoList: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, habitInfoList)
        habitListView.adapter = adapter
    }

    // Show habits for the selected date
    private fun showHabitsForDate(date: Long) {
        val userId = auth.currentUser?.uid
        val selectedDateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(date))

        db.collection("habits")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val habitsOnDate = documents.filter { document ->
                    val startDate = document.getString("startDate")
                    val endDate = document.getString("endDate")
                    isDateExactMatch(selectedDateStr, startDate, endDate)
                }

                if (habitsOnDate.isEmpty()) {
                    Toast.makeText(this, "No habits on this date", Toast.LENGTH_SHORT).show()
                } else {
                    val habitNames = habitsOnDate.map { it.getString("name") ?: "Unnamed Habit" }
                    showHabitDialog(habitNames, selectedDateStr)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch habits: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Check if the selected date exactly matches the start or end date
    private fun isDateExactMatch(date: String, startDate: String?, endDate: String?): Boolean {
        return date == startDate || date == endDate
    }

    // Show a dialog with habit names
    private fun showHabitDialog(habitNames: List<String>, selectedDateStr: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Habits for $selectedDateStr")

        val habitListMessage = habitNames.joinToString("\n")
        builder.setMessage(habitListMessage)

        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}

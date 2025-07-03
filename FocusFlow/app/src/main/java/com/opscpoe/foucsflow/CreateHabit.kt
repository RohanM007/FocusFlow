package com.opscpoe.foucsflow

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateHabit : AppCompatActivity() {

    // Global variables
    private lateinit var btnStartHabit: Button
    private lateinit var habitStartDate: EditText
    private lateinit var habitEndDate: EditText
    private lateinit var habitName: EditText
    private lateinit var habitDesc: EditText
    private lateinit var habitDuration: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Date variables
    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)

        // Initialize views
        habitName = findViewById(R.id.habitName)
        habitDesc = findViewById(R.id.habitDesc)
        habitStartDate = findViewById(R.id.habitStartDate)
        habitEndDate = findViewById(R.id.habitEndDate)
        btnStartHabit = findViewById(R.id.btnStartHabit)
        habitDuration = findViewById(R.id.habitDuration)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Set up listeners for date pickers
        habitStartDate.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                habitStartDate.setText(date)
            }
        }

        habitEndDate.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                habitEndDate.setText(date)
            }
        }

        // Listener for duration picker
        habitDuration.setOnClickListener {
            showDurationPicker()
        }

        // Listener for saving habit
        btnStartHabit.setOnClickListener {
            saveToFirebase()
        }
    }

    // Show DatePicker dialog and return selected date
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                onDateSelected(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    // Show custom duration picker with hours and minutes
    private fun showDurationPicker() {
        val dialog = android.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.duration_picker_dialog, null)
        dialog.setView(dialogView)

        val numberPickerHours = dialogView.findViewById<NumberPicker>(R.id.numberPickerHours)
        val numberPickerMinutes = dialogView.findViewById<NumberPicker>(R.id.numberPickerMinutes)

        // Set min/max values for pickers
        numberPickerHours.minValue = 0
        numberPickerHours.maxValue = 23
        numberPickerMinutes.minValue = 0
        numberPickerMinutes.maxValue = 59

        dialog.setPositiveButton("OK") { _, _ ->
            val hours = numberPickerHours.value
            val minutes = numberPickerMinutes.value

            val totalMinutes = (hours * 60) + minutes
            habitDuration.setText("$totalMinutes minutes")
            habitDuration.tag = totalMinutes // Store raw value
        }
        dialog.setNegativeButton("Cancel", null)

        dialog.show()
    }

    // Save habit to Firebase
    private fun saveToFirebase() {
        val habitNameValue = habitName.text.toString()
        val habitDescValue = habitDesc.text.toString()
        val habitStartDateValue = habitStartDate.text.toString()
        val habitEndDateValue = habitEndDate.text.toString()


        // Retrieve duration in minutes from tag
        val habitDurationValue = habitDuration.tag as? Int ?: 0

        // Validate input fields
        if (habitNameValue.isEmpty() || habitDescValue.isEmpty() ||
            habitStartDateValue.isEmpty() || habitEndDateValue.isEmpty()
        ) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Get current user's UID
        val user = auth.currentUser
        if (user != null) {
            val userUid = user.uid

            // Create a HabitModel instance
            val habit = HabitModel(
                id = UUID.randomUUID().toString(),
                name = habitNameValue,
                description = habitDescValue,
                startDate = habitStartDateValue,
                endDate = habitEndDateValue,
                duration = habitDurationValue.toString(),
                userId = userUid
            )

            // Save habit to Firestore
            db.collection("habits")
                .document(habit.id)
                .set(habit)
                .addOnSuccessListener {
                    Toast.makeText(this, "Habit saved successfully", Toast.LENGTH_SHORT).show()

                    // Call the method to add the habit's end date to the calendar
                    addEventToCalendar(
                        habitNameValue,
                        habitDescValue,
                        habitStartDateValue,
                        habitEndDateValue
                    )

                    setResult(RESULT_OK)
                    finish() // Close activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save habit: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    // Method to add the habit end date to the calendar
    private fun addEventToCalendar(
        name: String,
        description: String,
        startDate: String,
        endDate: String
    ) {
        try {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            // Parse start and end dates
            val startMillis = dateFormat.parse(startDate)?.time ?: 0
            val endMillis = dateFormat.parse(endDate)?.time ?: 0

            // Create an intent to insert an event into the calendar
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = android.provider.CalendarContract.Events.CONTENT_URI
                putExtra(android.provider.CalendarContract.Events.TITLE, name)
                putExtra(android.provider.CalendarContract.Events.DESCRIPTION, description)
                putExtra(android.provider.CalendarContract.Events.DTSTART, startMillis)
                putExtra(android.provider.CalendarContract.Events.DTEND, endMillis)
                putExtra(
                    android.provider.CalendarContract.Events.EVENT_TIMEZONE,
                    TimeZone.getDefault().id
                )
            }

            // Start the calendar intent
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Calendar app found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Failed to add event to calendar: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    // Data class for HabitModel
    data class HabitModel(
        val id: String = UUID.randomUUID().toString(),
        val name: String = "",
        val description: String = "",
        val startDate: String = "",
        val endDate: String = "",
        var duration: String = "",
        val userId: String = "",
        var remainingMinutes: Int? = null,

    )
}
package com.opscpoe.foucsflow.ui.home


import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.opscpoe.foucsflow.CreateHabit
import com.opscpoe.foucsflow.HabitAdapter
import com.opscpoe.foucsflow.R

class HomeFragment : Fragment() {

    private lateinit var habitAdapter: HabitAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var habits: MutableList<CreateHabit.HabitModel>

    // Define a request code for starting the CreateHabit activity
    companion object {
        const val REQUEST_CODE_CREATE_HABIT = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView and Firestore
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHabits)
        recyclerView.layoutManager = LinearLayoutManager(context)
        habits = mutableListOf()

        val context = requireContext()
        habitAdapter = HabitAdapter(habits, context)
        recyclerView.adapter = habitAdapter

        db = FirebaseFirestore.getInstance()

        // Fetch habits from Firestore
        refreshHabits()

        // Check and request notification permission (Android 13+)
        checkAndRequestNotificationPermission()

        // Subscribe to daily reminders topic
        subscribeToDailyReminders()

        return view
    }

    private fun refreshHabits() {
        habits.clear()

        db.collection("habits")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(context, "Failed to load habits: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    habits.clear()
                    for (document in snapshots) {
                        val habit = document.toObject(CreateHabit.HabitModel::class.java)
                        habits.add(habit)
                    }
                    habitAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_HABIT && resultCode == RESULT_OK) {
            refreshHabits()
        }
    }

    // Method to check and request notification permission on Android 13+
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(requireContext(), POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) -> {
                    Toast.makeText(context, "We need notification permission to send reminders.", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    // Register permission launcher for notification permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Subscribe users to a daily reminder topic for push notifications
    private fun subscribeToDailyReminders() {
        FirebaseMessaging.getInstance().subscribeToTopic("daily_reminder")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) {
                    "Subscribed to daily reminders"
                } else {
                    "Subscription failed"
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }
}

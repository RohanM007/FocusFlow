package com.opscpoe.foucsflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Diary : AppCompatActivity() {

    // Variables
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var diaryEntryEditText: EditText
    private lateinit var diaryTitleEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var currentDateTextView: TextView
    private lateinit var pastEntriesListView: ListView
    private lateinit var pastEntriesAdapter: ArrayAdapter<String>
    private var selectedEntryId: String? = null // To store the selected entry ID for deletion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        diaryEntryEditText = findViewById(R.id.etDiaryEntry)
        diaryTitleEditText = findViewById(R.id.etDiaryTitle)
        saveButton = findViewById(R.id.btnSaveDiary)
        deleteButton = findViewById(R.id.btnDelteDiaryEntry) // Find the delete button
        currentDateTextView = findViewById(R.id.tvCurrentDate)
        pastEntriesListView = findViewById(R.id.lvPastEntries)

        // Set current date
        val currentDate = getCurrentDate()
        currentDateTextView.text = "Date: $currentDate"

        // Set up ListView adapter
        pastEntriesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        pastEntriesListView.adapter = pastEntriesAdapter

        // Save button click listener
        saveButton.setOnClickListener {
            val diaryTitle = diaryTitleEditText.text.toString().trim()
            val diaryEntry = diaryEntryEditText.text.toString().trim()

            if (diaryTitle.isNotEmpty() && diaryEntry.isNotEmpty()) {
                saveDiaryEntry(currentDate, diaryTitle, diaryEntry)
            } else {
                Toast.makeText(this, "Please enter a title and diary entry", Toast.LENGTH_SHORT).show()
            }
        }

        // ListView item click listener to select an entry
        pastEntriesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedEntry = pastEntriesAdapter.getItem(position)
            Toast.makeText(this, "Selected entry: $selectedEntry", Toast.LENGTH_SHORT).show()

            // Retrieve the document ID of the selected entry from Firestore
            firestore.collection("diaryEntries")
                .get()
                .addOnSuccessListener { result ->
                    val selectedDocument = result.documents[position]
                    selectedEntryId = selectedDocument.id // Store the document ID
                }
        }

        // Delete button click listener
        deleteButton.setOnClickListener {
            if (selectedEntryId != null) {
                deleteDiaryEntry(selectedEntryId!!)
            } else {
                Toast.makeText(this, "Please select an entry to delete", Toast.LENGTH_SHORT).show()
            }
        }

        // Load past diary entries
        loadPastDiaryEntries()
    }

    // Function to get the current date in dd/MM/yyyy format
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    // Function to save the diary entry to Firebase Firestore
    private fun saveDiaryEntry(date: String, title: String, entry: String) {
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val diaryEntry = hashMapOf(
                "userId" to userId, // Store the user's UID
                "date" to date,
                "title" to title,
                "entry" to entry
            )

            firestore.collection("diaryEntries")
                .add(diaryEntry)
                .addOnSuccessListener {
                    Toast.makeText(this, "Diary entry saved successfully", Toast.LENGTH_SHORT).show()
                    diaryEntryEditText.text.clear() // Clear the EditText after saving
                    diaryTitleEditText.text.clear() // Clear the title EditText
                    loadPastDiaryEntries() // Refresh the list of past entries
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving entry: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to delete the selected diary entry from Firestore
    private fun deleteDiaryEntry(entryId: String) {
        firestore.collection("diaryEntries").document(entryId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Diary entry deleted", Toast.LENGTH_SHORT).show()
                loadPastDiaryEntries() // Refresh the list after deletion
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to load past diary entries from Firestore
    private fun loadPastDiaryEntries() {
        val currentUser = firebaseAuth.currentUser //getting the current user
        val userId = currentUser?.uid //getting the users uid

        if(userId !=null) {
            firestore.collection("diaryEntries")
                .whereEqualTo("userId", userId) //query current user entries
                .get()
                .addOnSuccessListener { result ->
                    val diaryEntries = ArrayList<String>()
                    for (document in result) {
                        val date = document.getString("date")
                        val title = document.getString("title")
                        val entry = document.getString("entry")
                        if (date != null && title != null && entry != null) {
                            diaryEntries.add("$date: $title - $entry")
                        }
                    }
                    pastEntriesAdapter.clear()
                    pastEntriesAdapter.addAll(diaryEntries)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading entries: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }else{
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}


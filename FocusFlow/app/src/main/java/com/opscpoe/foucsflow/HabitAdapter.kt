package com.opscpoe.foucsflow

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore

class HabitAdapter(
    private val habits: MutableList<CreateHabit.HabitModel>,
    private val context: Context
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtHabitName: EditText = itemView.findViewById(R.id.edtHabitname)
        private val detailsLayout: LinearLayout = itemView.findViewById(R.id.detailsLayout)
        private val edtDescription: EditText = itemView.findViewById(R.id.edtDescription)
        private val edtTotalHours: EditText = itemView.findViewById(R.id.edtTotalHours)
        private val pieChart: PieChart = itemView.findViewById(R.id.pieChart)
        private val timeSpentView: TextView = itemView.findViewById(R.id.Timespent)
        private val btnCompleteHabit: Button = itemView.findViewById(R.id.btnCompleteHabit)
        private val btnDeleteHabit: Button = itemView.findViewById(R.id.btnDeleteHabit)
        private val btnSaveProgress: Button = itemView.findViewById(R.id.btnSaveProgress)

        private var remainingMinutes = 0  // Track remaining duration in minutes
        private var totalMinutes = 0  // Store the total habit minutes

        fun bind(habit: CreateHabit.HabitModel) {
            edtHabitName.setText(habit.name)
            edtTotalHours.setText(habit.duration)
            edtDescription.setText(habit.description)

            // Convert habit duration from hours to minutes and store it
            totalMinutes = habit.duration.toInt()
            remainingMinutes = habit.remainingMinutes ?: totalMinutes

            updatePieChart(totalMinutes, remainingMinutes)

            edtHabitName.setOnClickListener {
                detailsLayout.visibility =
                    if (detailsLayout.visibility == View.GONE) View.VISIBLE else View.GONE
            }



            timeSpentView.setOnClickListener {
                showDurationPicker()
            }

            btnCompleteHabit.setOnClickListener {
                completeHabit(habit)
            }
            btnDeleteHabit.setOnClickListener {
                deleteHabit(habit)
            }
            btnSaveProgress.setOnClickListener {
                saveProgressToFirestore(habit)
            }
        }

        private fun showDurationPicker() {
            val dialog = AlertDialog.Builder(context)
            val dialogView = LayoutInflater.from(context).inflate(R.layout.duration_picker_dialog, null)
            dialog.setView(dialogView)

            val numberPickerHours = dialogView.findViewById<NumberPicker>(R.id.numberPickerHours)
            val numberPickerMinutes = dialogView.findViewById<NumberPicker>(R.id.numberPickerMinutes)

            numberPickerHours.minValue = 0
            numberPickerHours.maxValue = 23
            numberPickerMinutes.minValue = 0
            numberPickerMinutes.maxValue = 59

            dialog.setPositiveButton("OK") { _, _ ->
                val hours = numberPickerHours.value
                val minutes = numberPickerMinutes.value
                val totalSpentMinutes = (hours * 60) + minutes

                timeSpentView.text = "$totalSpentMinutes minutes"

                remainingMinutes -= totalSpentMinutes
                if (remainingMinutes < 0) remainingMinutes = 0

                updatePieChart(totalMinutes, remainingMinutes)
            }
            dialog.setNegativeButton("Cancel", null)
            dialog.show()
        }

        private fun updatePieChart(totalMinutes: Int, remainingMinutes: Int) {
            val workedMinutes = totalMinutes - remainingMinutes

            val entries = listOf(
                PieEntry(remainingMinutes.toFloat(), "Remaining"),
                PieEntry(workedMinutes.toFloat(), "Worked")
            )

            val dataSet = PieDataSet(entries, "Progress")
            dataSet.colors = listOf(
                context.getColor(R.color.red),
                context.getColor(R.color.blue)
            )

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.invalidate()  // Refresh the chart

            //checking if the goal is completed
            if(remainingMinutes <= 0){
                showBadge()

            }
        }

        private fun saveProgressToFirestore(habit: CreateHabit.HabitModel) {
            habit.remainingMinutes = remainingMinutes

            FirebaseFirestore.getInstance()
                .collection("habits")
                .document(habit.id)
                .set(habit)
                .addOnSuccessListener {
                    Toast.makeText(context, "Progress saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save progress.", Toast.LENGTH_SHORT).show()
                }
        }

        private fun completeHabit(habit: CreateHabit.HabitModel) {
            val position = adapterPosition  // Get the current habit's position
            if (position != RecyclerView.NO_POSITION) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Complete Habit")
                builder.setMessage("Are you sure you want to mark '${habit.name}' as complete and delete it?")

                builder.setPositiveButton("Yes") { dialog, _ ->
                    // Delete the habit from Firestore
                    FirebaseFirestore.getInstance().collection("habits")
                        .document(habit.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Habit completed and deleted!", Toast.LENGTH_SHORT).show()

                            // Remove the habit from the list and notify adapter of removal
                            habits.removeAt(position)
                            notifyItemRemoved(position)

                            // Launch Badge activity
                            val intent = Intent(context, Badge::class.java)
                            intent.putExtra("habitName", habit.name)
                            context.startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to delete habit: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    dialog.dismiss()
                }

                builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                builder.show()
            }
        }



        private fun deleteHabit(habit: CreateHabit.HabitModel) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Habit")
            builder.setMessage("Are you sure you want to delete '${habit.name}'?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    habits.removeAt(position)
                    notifyItemRemoved(position)
                    FirebaseFirestore.getInstance().collection("habits").document(habit.id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Habit deleted!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to delete habit: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            builder.show()
        }

        private fun showBadge() {
            Toast.makeText(context, "Congratulations! You've completed your habit!", Toast.LENGTH_SHORT).show()

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val habitToDelete = habits[position]

                FirebaseFirestore.getInstance().collection("habits")
                    .document(habitToDelete.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Habit deleted!", Toast.LENGTH_SHORT).show()
                        habits.removeAt(position)  // Ensure only this habit is removed
                        notifyItemRemoved(position)  // Notify adapter of removal
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to delete habit: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                // Launch Badge activity after deletion
                val intent = Intent(context, Badge::class.java)
                context.startActivity(intent)
            }
        }

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.habit_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size
}

package com.opscpoe.foucsflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.os.Handler


class Badge : AppCompatActivity() {

    // Declare variables for the views
    private lateinit var badgeImageView: ImageView
    private lateinit var congratulationsText: TextView

    private val delayMillis: Long = 2000 // Duration for the badge display
    private val totalDisplayTime: Long = delayMillis + 500 + 500 // Total time for the badge display with animations

    // List of badge drawable resources
    private val badgeImages = listOf(
        R.drawable.insignia,
        R.drawable.star,
        R.drawable.medal,
        R.drawable.congratulation,
        R.drawable.like,
        R.drawable.congratulation2,
        R.drawable.firework,
        R.drawable.highspirit
    )

    // List of congratulatory messages
    private val congratulatoryMessages = listOf(
        "Congratulations on completing your habit!",
        "Well done! You've made great progress!",
        "Awesome job! Keep up the good work!",
        "Fantastic! You're one step closer to your goal!",
        "Bravo! You've achieved another milestone!",
        "Great work! You're making positive changes!",
        "Amazing effort! Stay motivated!",
        "Kudos! Your hard work is paying off!",
        "Hooray! You're doing wonderfully!",
        "Excellent! You're on the right track!",
        "Superb! Your dedication is inspiring!",
        "Outstanding! Keep pushing forward!",
        "Remarkable! You're achieving greatness!",
        "Way to go! Every effort counts!",
        "Incredible! You're making a difference!",
        "Wonderful! You're building healthy habits!",
        "Impressive! You're committed to your growth!",
        "Cheers! You're transforming your life!",
        "Outstanding! You're a habit champion!",
        "Keep it up! Your consistency is impressive!",
        "You're unstoppable! Keep shining!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        // Initialize views from the activity layout
        congratulationsText = findViewById(R.id.congratulationsText)
        badgeImageView = findViewById(R.id.imageView3)

        // Set the list of random congratulatory messsages
        val randomMessage = congratulatoryMessages.random()
        congratulationsText.text = randomMessage

        // Pick a random badge from the list
        val randomBadge = badgeImages.random()
        badgeImageView.setImageResource(randomBadge)

        // Show the badge using an animation
        badgeImageView.alpha = 0f
        badgeImageView.visibility = View.VISIBLE
        badgeImageView.animate().alpha(1f).setDuration(500).withEndAction {
            badgeImageView.postDelayed({
                badgeImageView.animate().alpha(0f).setDuration(500).withEndAction {
                    badgeImageView.visibility = View.GONE
                }
            }, 5000) // Adjust duration as needed
        }

        // Animate the text (TextView) appearing
        congratulationsText.alpha = 0f
        congratulationsText.visibility = View.VISIBLE
        congratulationsText.animate().alpha(1f).setDuration(500).start()

        // Close the badge activity after totalDisplayTime
        Handler().postDelayed({
            finish() // Close the activity
        }, totalDisplayTime)
    }
}

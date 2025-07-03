package com.opscpoe.foucsflow


import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.opscpoe.foucsflow.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // Timer delay for splash
    var delay: Long = 8000

    // Base URL for quotes
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()

        // Start the next activity
        animateProgressBar()

        // Start the next activity after the delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }, delay)
    }

    private fun getData() {

        RetroInstance.someInterface.getData().enqueue(object : Callback<List<MyDataItem>> {
            override fun onResponse(
                call: Call<List<MyDataItem>>,
                response: Response<List<MyDataItem>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val quote = response.body()!![0] // Assuming the first quote is what you want
                    val quoteText = quote.q  // The quote from API
                    val authorText = quote.a  // The author from API

                    // Format the text to show "quote" - author
                    val formattedText = "\"$quoteText\" - $authorText"

                    // Set the formatted text to the TextView
                    binding.tvQuote.text = formattedText
                }
            }

            override fun onFailure(call: Call<List<MyDataItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch API data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun animateProgressBar() {
        val progressBar = binding.progressBarHorizontal
        val progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        progressAnimator.duration = delay
        progressAnimator.start()
    }

}

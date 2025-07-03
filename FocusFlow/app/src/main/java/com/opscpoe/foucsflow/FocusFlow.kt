package com.opscpoe.foucsflow

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.opscpoe.foucsflow.databinding.ActivityFocusFlowBinding
import java.util.Locale


class FocusFlow : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityFocusFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFocusFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarFocusFlow.toolbar)

        // FAB click listener to navigate to CreateHabit activity
        binding.appBarFocusFlow.fab.setOnClickListener { view ->
            val intent = Intent(this, CreateHabit::class.java)
            startActivity(intent)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_focus_flow)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Handle clicks on navigation items
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Create -> {
                    // Navigate to the CreateHabit activity
                    val intent = Intent(this@FocusFlow, CreateHabit::class.java)
                    startActivity(intent)
                    true
                }
                R.id.Diary -> {
                    // Navigate to the Diary activity
                    val intent = Intent(this@FocusFlow, LockDiary::class.java)
                    startActivity(intent)
                    true
                }

                R.id.aboutUs ->
                {
                    val intent = Intent(this@FocusFlow, AboutUs::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_slideshow -> {
                    // Call logout function
                    logoutUser()
                    true
                }

                R.id.Calendar ->
                {
                        val intent = Intent(this@FocusFlow, CalendarActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.focus_flow, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_focus_flow)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Navigate to the Themes activity when the menu item is clicked
                val intent = Intent(this, Themes::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                // Handle settings action
                true
            }
            R.id.language_english -> {
                setLocale("en") // Set app to English
                true
            }
            R.id.language_afrikaans -> {
                setLocale("af") // Set app to Afrikaans
                true
            }
            R.id.language_zulu -> {
                setLocale("zu") // Set app to Zulu
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Restart the current activity to apply changes
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun logoutUser() {

        // Navigate to Login activity
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
        startActivity(intent)
        finish()
    }
}
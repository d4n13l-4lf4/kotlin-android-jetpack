package com.example.playpennydrop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.containerFragment) as NavHostFragment
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val themeId = when (prefs.getString("theme", "AppTheme")) {
            "Crew" -> R.style.Theme_PlayPennyDrop
            "Kotlin" -> R.style.Kotlin
            else -> R.style.Theme_PlayPennyDrop
        }

        setTheme(themeId)

        this.navController = navHostFragment.navController

        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(this.navController)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val appBarConfiguration = AppBarConfiguration(bottomNav.menu)
        setupActionBarWithNavController(
            this.navController,
            appBarConfiguration
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.options, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = if (this::navController.isInitialized) {
        item.onNavDestinationSelected(this.navController) || super.onOptionsItemSelected(item)
    } else false

    override fun onSupportNavigateUp(): Boolean = (this::navController.isInitialized && this.navController.navigateUp()) || super.onSupportNavigateUp()
}
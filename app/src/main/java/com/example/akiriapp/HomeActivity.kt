package com.example.akiriapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.akiriapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_courses -> true
                R.id.nav_forum -> {
                    startActivity(android.content.Intent(this, com.example.akiriapp.forum.ForumActivity::class.java))
                    true
                }
                R.id.nav_my_learning -> {
                    startActivity(android.content.Intent(this, com.example.akiriapp.learning.MyLearningActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(android.content.Intent(this, com.example.akiriapp.settings.SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}

package com.example.akiriapp.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.auth.LoginActivity
import com.example.akiriapp.data.DataSeeder
import com.example.akiriapp.data.repository.AuthRepository
import com.example.akiriapp.databinding.ActivitySettingsBinding
import com.example.akiriapp.trainer.TrainerDashboardActivity
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupLogout()
        setupSeedData()
        loadUserProfile()
        setupTrainerDashboard()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            authRepository.getCurrentUserProfile().onSuccess { user ->
                binding.tvUserName.text = user.fullName
                binding.tvUserEmail.text = user.email
                
                // Set initials
                val initials = user.fullName
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")
                    .uppercase()
                
                binding.tvInitials.text = initials.ifEmpty { "?" }
                
                // Show trainer dashboard button if user is trainer
                if (user.role == "trainer") {
                    binding.btnTrainerDashboard.visibility = View.VISIBLE
                }
            }.onFailure {
                // Handle error or show placeholder
            }
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            authRepository.signOut()
            
            // Navigate back to Login and clear stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupSeedData() {
        binding.btnSeedData.setOnClickListener {
            lifecycleScope.launch {
                val seeder = DataSeeder()
                seeder.seedCourses()
                
                val userId = authRepository.currentUser?.uid
                if (userId != null) {
                    authRepository.getCurrentUserProfile().onSuccess { user ->
                        seeder.seedForumPosts(userId, user.fullName)
                    }
                }
                
                Toast.makeText(this@SettingsActivity, "Données de test ajoutées ! \uD83D\uDE80", Toast.LENGTH_SHORT).show()
                
                // Disable button to prevent spam
                binding.btnSeedData.isEnabled = false
                binding.btnSeedData.text = "Données générées"
            }
        }
    }

    private fun setupTrainerDashboard() {
        binding.btnTrainerDashboard.setOnClickListener {
            startActivity(Intent(this, TrainerDashboardActivity::class.java))
        }
    }
}

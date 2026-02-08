package com.example.akiriapp.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.akiriapp.databinding.ActivityRoleSelectionBinding

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardStudent.setOnClickListener {
            navigateToAuth("student")
        }

        binding.cardTrainer.setOnClickListener {
            navigateToAuth("trainer")
        }
    }

    private fun navigateToAuth(role: String) {
        // Navigate to SignUpActivity with the selected role
        val intent = Intent(this, SignUpActivity::class.java)
        intent.putExtra("SELECTED_ROLE", role)
        startActivity(intent)
    }
}

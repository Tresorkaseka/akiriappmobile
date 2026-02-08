package com.example.akiriapp.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.HomeActivity
import com.example.akiriapp.databinding.ActivitySignUpBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val authRepository = AuthRepository()
    private var selectedRole: String = "student" // Default role

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get role passed from RoleSelectionActivity
        selectedRole = intent.getStringExtra("SELECTED_ROLE") ?: "student"

        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            performSignUp()
        }
    }

    private fun performSignUp() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmailSignUp.text.toString().trim()
        val password = binding.etPasswordSignUp.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSignUp.isEnabled = false

        lifecycleScope.launch {
            val result = authRepository.signUp(email, password, fullName, selectedRole)
            result.onSuccess {
                Toast.makeText(this@SignUpActivity, "Compte créé avec succès!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }.onFailure { exception ->
                binding.btnSignUp.isEnabled = true
                Toast.makeText(this@SignUpActivity, "Erreur: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finishAffinity()
    }
}

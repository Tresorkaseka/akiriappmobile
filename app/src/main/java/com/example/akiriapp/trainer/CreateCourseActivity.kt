package com.example.akiriapp.trainer

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.data.repository.AuthRepository
import com.example.akiriapp.data.repository.CourseRepository
import com.example.akiriapp.databinding.ActivityCreateCourseBinding
import kotlinx.coroutines.launch

class CreateCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCourseBinding
    private val authRepository = AuthRepository()
    private val courseRepository = CourseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategoryDropdown()
        setupCreateButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupCategoryDropdown() {
        val categories = courseRepository.getCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupCreateButton() {
        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val priceStr = binding.etPrice.text.toString()
            val category = binding.actCategory.text.toString()

            if (title.isBlank() || description.isBlank() || priceStr.isBlank() || category.isBlank()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toIntOrNull() ?: 0
            
            createCourse(title, description, price, category)
        }
    }

    private fun createCourse(title: String, description: String, price: Int, category: String) {
        val userId = authRepository.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            authRepository.getCurrentUserProfile().onSuccess { user ->
                val newCourse = Course(
                    title = title,
                    description = description,
                    price = price,
                    instructorId = userId,
                    instructorName = user.fullName,
                    category = category,
                    duration = "0h 0min", // Default
                    lessonCount = 0
                )
                
                courseRepository.createCourse(newCourse).onSuccess {
                    Toast.makeText(this@CreateCourseActivity, "Cours créé avec succès !", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { e ->
                    Toast.makeText(this@CreateCourseActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

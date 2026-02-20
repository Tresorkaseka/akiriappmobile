package com.example.akiriapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akiriapp.adapter.CategoryAdapter
import com.example.akiriapp.adapter.CourseAdapter
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.databinding.ActivityHomeBinding
import com.example.akiriapp.course.CourseDetailsActivity
import com.example.akiriapp.forum.ForumActivity
import com.example.akiriapp.learning.MyLearningActivity
import com.example.akiriapp.settings.SettingsActivity

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.akiriapp.data.repository.CourseRepository
import com.example.akiriapp.data.repository.StorageRepository
import android.util.Log
import android.net.Uri
import com.example.akiriapp.R

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularCourseAdapter: CourseAdapter
    private lateinit var recommendedCourseAdapter: CourseAdapter
    private val courseRepository = CourseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupBottomNavigation()
        loadDummyData()
        
        // TEMPORARY SCRIPT TO UPLOAD NEW IMAGES FOR EXISTING COURSES
        uploadMissingThumbnails()
        
        binding.imgLogo.setOnClickListener { 
            // Refresh or scroll to top
        }
    }

    private fun setupAdapters() {
        // Categories
        val categories = listOf("Tout", "Programmation", "Design", "Business", "Marketing", "Finance", "Santé")
        categoryAdapter = CategoryAdapter(categories) { category ->
            Toast.makeText(this, "Selected: $category", Toast.LENGTH_SHORT).show()
            // Filter logic would go here
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Popular Courses
        popularCourseAdapter = CourseAdapter { course ->
            openCourseDetails(course)
        }
        binding.rvPopular.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularCourseAdapter
        }

        // Recommended Courses
        recommendedCourseAdapter = CourseAdapter { course ->
            openCourseDetails(course)
        }
        binding.rvRecommended.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedCourseAdapter
        }
    }

    private fun openCourseDetails(course: Course) {
        val intent = Intent(this, CourseDetailsActivity::class.java)
        intent.putExtra("COURSE_ID", course.id)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_forum -> {
                    startActivity(Intent(this, ForumActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_my_learning -> {
                    startActivity(Intent(this, MyLearningActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadDummyData() {
        lifecycleScope.launch {
            // Load Popular
            courseRepository.getPopularCourses(5).onSuccess { courses ->
                if (courses.isNotEmpty()) {
                    popularCourseAdapter.submitList(courses)
                } else {
                    Log.d("HomeActivity", "No popular courses found, loading recents")
                    // If no popular courses, fall back to recent ones to ensure learners see something
                    courseRepository.getCourses(limit = 5).onSuccess { recentCourses ->
                         popularCourseAdapter.submitList(recentCourses)
                    }
                }
            }.onFailure { e ->
                Log.e("HomeActivity", "Error loading popular: ${e.message}")
            }

            // Load Recommended
            courseRepository.getCourses(limit = 10).onSuccess { courses ->
                if (courses.isNotEmpty()) {
                    recommendedCourseAdapter.submitList(courses)
                } else {
                    Log.d("HomeActivity", "No recommended courses found")
                    recommendedCourseAdapter.submitList(emptyList())
                }
            }.onFailure { e ->
                Log.e("HomeActivity", "Error loading recommended: ${e.message}")
            }
        }
    }

    private fun uploadMissingThumbnails() {
        lifecycleScope.launch {
            val storageRepo = StorageRepository()
            val categoriesMap = mapOf(
                "Programmation" to R.drawable.generated_course_programmation,
                "Business" to R.drawable.generated_course_business,
                "Design" to R.drawable.generated_course_design,
                "Langues" to R.drawable.generated_course_languages
            )
            
            courseRepository.getCourses(limit = 100).onSuccess { courses ->
                courses.filter { it.thumbnailUrl == null }.forEach { course ->
                    val resId = categoriesMap[course.category] ?: R.drawable.generated_course_programmation
                    
                    try {
                        val bitmap = android.graphics.BitmapFactory.decodeResource(resources, resId)
                        val baos = java.io.ByteArrayOutputStream()
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
                        val data = baos.toByteArray()
                        
                        Log.d("HomeActivity", "Uploading thumbnail for course ${course.title}")
                        storageRepo.uploadCourseThumbnail(data).onSuccess { downloadUrl ->
                            courseRepository.updateCourse(course.id, mapOf("thumbnailUrl" to downloadUrl))
                            Log.d("HomeActivity", "Successfully updated ${course.title} with generic cover.")
                        }.onFailure { e ->
                            Log.e("HomeActivity", "Failed to upload for ${course.title}: ${e.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("HomeActivity", "Error processing image for ${course.title}: ${e.message}")
                    }
                }
            }
        }
    }
}

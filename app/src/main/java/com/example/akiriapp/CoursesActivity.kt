package com.example.akiriapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akiriapp.adapter.CourseAdapter
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.databinding.ActivityCoursesBinding // Assuming XML is activity_courses.xml, this will be generated
import com.example.akiriapp.R

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.akiriapp.data.repository.CourseRepository
import android.widget.Toast
import android.view.View

class CoursesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoursesBinding
    private val courseRepository = CourseRepository()
    private val courseAdapter = CourseAdapter { course ->
        val intent = Intent(this, com.example.akiriapp.course.CourseDetailsActivity::class.java)
        intent.putExtra("COURSE_ID", course.id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBottomNavigation()
        loadCourses()
    }

    private fun setupRecyclerView() {
        binding.rvAllCourses.apply {
            layoutManager = LinearLayoutManager(this@CoursesActivity)
            adapter = courseAdapter
        }
    }

    private fun setupBottomNavigation() {
        // Removed default selected item since it was nav_courses
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_forum -> {
                    startActivity(Intent(this, com.example.akiriapp.forum.ForumActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_my_learning -> {
                    startActivity(Intent(this, com.example.akiriapp.learning.MyLearningActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, com.example.akiriapp.settings.SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadCourses() {
        binding.rvAllCourses.visibility = View.GONE
        // Show a loader if you have one, or just wait.
        
        lifecycleScope.launch {
            courseRepository.getCourses().onSuccess { courses ->
                if (courses.isNotEmpty()) {
                    binding.rvAllCourses.visibility = View.VISIBLE
                    courseAdapter.submitList(courses)
                } else {
                    Toast.makeText(this@CoursesActivity, "Aucun cours trouvé", Toast.LENGTH_SHORT).show()
                }
            }.onFailure { e ->
                Toast.makeText(this@CoursesActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

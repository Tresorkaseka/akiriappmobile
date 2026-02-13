package com.example.akiriapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akiriapp.adapter.CourseAdapter
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.databinding.ActivityCoursesBinding // Assuming XML is activity_courses.xml, this will be generated
import com.example.akiriapp.R

class CoursesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoursesBinding
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
        binding.bottomNavigation.selectedItemId = R.id.nav_courses
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_courses -> true
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
        // Dummy data for now
        val courses = listOf(
            Course("1", "Introduction to Python", "Master Python basics in 4 weeks", 15000, "inst1", "Dr. Kanyinda", "Programming", 4.5f, 120, null, "10h", 24, 150, 0, 0, R.drawable.course_python),
            Course("2", "Digital Marketing 101", "Learn SEO, SEM, and Social Media", 20000, "inst2", "Sarah Mbombo", "Business", 4.2f, 85, null, "8h", 15, 200, 0, 0, R.drawable.course_marketing),
            Course("3", "Mobile App Dev with Kotlin", "Build Android apps from scratch", 25000, "inst3", "Jean-Pierre Mukendi", "Development", 4.8f, 300, null, "20h", 45, 500, 0, 0, R.drawable.course_kotlin),
            Course("4", "Financial Literacy", "Manage your personal finances", 10000, "inst4", "Banque Centrale", "Finance", 4.6f, 50, null, "5h", 10, 80, 0, 0, R.drawable.course_finance),
            Course("5", "Graphic Design Masterclass", "Photoshop, Illustrator, InDesign", 18000, "inst5", "Creative Studio", "Design", 4.7f, 150, null, "15h", 30, 120, 0, 0, R.drawable.course_design)
        )
        courseAdapter.submitList(courses)
    }
}

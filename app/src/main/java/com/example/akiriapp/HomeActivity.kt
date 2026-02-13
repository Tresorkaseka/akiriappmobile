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

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularCourseAdapter: CourseAdapter
    private lateinit var recommendedCourseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupBottomNavigation()
        loadDummyData()
        
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
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false) // Or horizontal if layout implies list
            // Based on layout XML, looks like vertical list? 
            // Wait, usually recommended is vertical list in home. Re-check XML layout manager if defined there or override here.
            // XML says LinearLayoutManager. Default is vertical. Let's keep it vertical for Recommended.
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
                R.id.nav_courses -> {
                    startActivity(Intent(this, CoursesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
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
        // Dummy Courses
        val popularCourses = listOf(
            Course("1", "Introduction to Python", "Master Python basics in 4 weeks", 15000, "inst1", "Dr. Kanyinda", "Programming", 4.5f, 120, null, "10h", 24, 150, 0, 0, R.drawable.course_python),
            Course("2", "Digital Marketing 101", "Learn SEO, SEM, and Social Media", 20000, "inst2", "Sarah Mbombo", "Marketing", 4.2f, 85, null, "8h", 15, 200, 0, 0, R.drawable.course_marketing),
            Course("3", "Mobile App Dev with Kotlin", "Build Android apps from scratch", 25000, "inst3", "Jean-Pierre Mukendi", "Development", 4.8f, 300, null, "20h", 45, 500, 0, 0, R.drawable.course_kotlin)
        )
        popularCourseAdapter.submitList(popularCourses)

        val recommendedCourses = listOf(
            Course("4", "Financial Literacy", "Manage your personal finances", 10000, "inst4", "Banque Centrale", "Finance", 4.6f, 50, null, "5h", 10, 80, 0, 0, R.drawable.course_finance),
            Course("5", "Graphic Design Masterclass", "Photoshop, Illustrator, InDesign", 18000, "inst5", "Creative Studio", "Design", 4.7f, 150, null, "15h", 30, 120, 0, 0, R.drawable.course_design),
            Course("3", "Mobile App Dev with Kotlin", "Build Android apps from scratch", 25000, "inst3", "Jean-Pierre Mukendi", "Development", 4.8f, 300, null, "20h", 45, 500, 0, 0, R.drawable.course_kotlin)
        )
        recommendedCourseAdapter.submitList(recommendedCourses)
    }
}

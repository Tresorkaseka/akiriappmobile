package com.example.akiriapp.trainer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akiriapp.adapter.CourseAdapter
import com.example.akiriapp.data.repository.AuthRepository
import com.example.akiriapp.data.repository.CourseRepository
import com.example.akiriapp.databinding.ActivityTrainerDashboardBinding
import kotlinx.coroutines.launch

class TrainerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainerDashboardBinding
    private val authRepository = AuthRepository()
    private val courseRepository = CourseRepository()
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadDashboardData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter { course ->
            // Open Edit Course logic here
            // val intent = Intent(this, EditCourseActivity::class.java)
            // intent.putExtra("COURSE_ID", course.id)
            // startActivity(intent)
            Toast.makeText(this, "Édition du cours : ${course.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvMyCourses.apply {
            layoutManager = LinearLayoutManager(this@TrainerDashboardActivity)
            adapter = courseAdapter
        }
    }

    private fun setupFab() {
        binding.fabCreateCourse.setOnClickListener {
             startActivity(Intent(this, CreateCourseActivity::class.java))
        }
    }

    private fun loadDashboardData() {
        val userId = authRepository.currentUser?.uid ?: return

        lifecycleScope.launch {
            // Load courses created by this instructor
            courseRepository.getCoursesByInstructor(userId).onSuccess { courses ->
                if (courses.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvMyCourses.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvMyCourses.visibility = View.VISIBLE
                    courseAdapter.submitList(courses)
                    
                    // Calculate quick stats
                    var totalStudents = 0
                    var totalRevenue = 0L // Use Long to prevent overflow
                    
                    courses.forEach { course ->
                        totalStudents += course.enrollmentCount
                        // Approximation: revenue = price * enrollments (ignoring discounts/platform fees for MVP)
                        totalRevenue += (course.price.toLong() * course.enrollmentCount)
                    }
                    
                    binding.tvTotalStudents.text = totalStudents.toString()
                    binding.tvTotalRevenue.text = "${totalRevenue} FC"
                }
            }.onFailure { e ->
                Toast.makeText(this@TrainerDashboardActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadDashboardData() // Refresh data when returning from Create/Edit screen
    }
}

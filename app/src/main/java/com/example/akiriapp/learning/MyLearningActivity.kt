package com.example.akiriapp.learning

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.R
import com.example.akiriapp.adapter.EnrollmentAdapter
import com.example.akiriapp.course.CourseDetailsActivity
import com.example.akiriapp.data.repository.AuthRepository
import com.example.akiriapp.data.repository.EnrollmentRepository
import com.example.akiriapp.databinding.ActivityMyLearningBinding
import kotlinx.coroutines.launch

class MyLearningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyLearningBinding
    private val authRepository = AuthRepository()
    private val enrollmentRepository = EnrollmentRepository()
    
    private lateinit var inProgressAdapter: EnrollmentAdapter
    private lateinit var completedAdapter: EnrollmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupFilterChips()
        setupSpotlightCard()
        loadUserData()
    }

    private fun setupAdapters() {
        inProgressAdapter = EnrollmentAdapter { enrollment ->
            navigateToCourse(enrollment.courseId)
        }
        completedAdapter = EnrollmentAdapter { enrollment ->
            navigateToCourse(enrollment.courseId)
        }
        
        binding.rvInProgress.adapter = inProgressAdapter
        binding.rvCompleted.adapter = completedAdapter
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(binding.chipAll.id) -> filterCourses("all")
                checkedIds.contains(binding.chipInProgress.id) -> filterCourses("in_progress")
                checkedIds.contains(binding.chipCompleted.id) -> filterCourses("completed")
            }
        }
    }

    private fun setupSpotlightCard() {
        binding.cardSpotlight.setOnClickListener {
            lifecycleScope.launch {
                val userId = authRepository.currentUser?.uid ?: return@launch
                enrollmentRepository.getLastAccessedEnrollment(userId).onSuccess { enrollment ->
                    enrollment?.let { navigateToCourse(it.courseId) }
                }
            }
        }
    }

    private fun loadUserData() {
        val userId = authRepository.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            // Load stats
            enrollmentRepository.getEnrollmentStats(userId).onSuccess { (inProgress, completed) ->
                binding.tvStats.text = getString(R.string.my_learning_stats, inProgress, completed)
            }

            // Load last accessed for spotlight
            enrollmentRepository.getLastAccessedEnrollment(userId).onSuccess { enrollment ->
                enrollment?.let {
                    binding.tvSpotlightTitle.text = it.courseTitle
                    binding.progressSpotlight.progress = it.progress
                    binding.tvSpotlightProgress.text = "${it.progress}% complété"
                }
            }

            // Load in-progress enrollments
            enrollmentRepository.getInProgressEnrollments(userId).onSuccess { enrollments ->
                inProgressAdapter.submitList(enrollments)
            }

            // Load completed enrollments
            enrollmentRepository.getCompletedEnrollments(userId).onSuccess { enrollments ->
                completedAdapter.submitList(enrollments)
            }
        }
    }

    private fun filterCourses(filter: String) {
        when (filter) {
            "all" -> {
                binding.rvInProgress.visibility = android.view.View.VISIBLE
                binding.rvCompleted.visibility = android.view.View.VISIBLE
            }
            "in_progress" -> {
                binding.rvInProgress.visibility = android.view.View.VISIBLE
                binding.rvCompleted.visibility = android.view.View.GONE
            }
            "completed" -> {
                binding.rvInProgress.visibility = android.view.View.GONE
                binding.rvCompleted.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun navigateToCourse(courseId: String) {
        val intent = Intent(this, CourseDetailsActivity::class.java)
        intent.putExtra("COURSE_ID", courseId)
        startActivity(intent)
    }
}

package com.example.akiriapp.learning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.akiriapp.R
import com.example.akiriapp.adapter.EnrollmentAdapter
import com.example.akiriapp.data.model.Enrollment
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

    private var allInProgress: List<Enrollment> = emptyList()
    private var allCompleted: List<Enrollment> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupFilterChips()
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
                checkedIds.contains(binding.chipAll.id) -> showSections("all")
                checkedIds.contains(binding.chipInProgress.id) -> showSections("in_progress")
                checkedIds.contains(binding.chipCompleted.id) -> showSections("completed")
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

            // Load in-progress enrollments
            enrollmentRepository.getInProgressEnrollments(userId).onSuccess { enrollments ->
                allInProgress = enrollments
                inProgressAdapter.submitList(enrollments)
                updateEmptyState()
            }

            // Load completed enrollments
            enrollmentRepository.getCompletedEnrollments(userId).onSuccess { enrollments ->
                allCompleted = enrollments
                completedAdapter.submitList(enrollments)
                updateEmptyState()
            }
        }
    }

    private fun updateEmptyState() {
        val isEmpty = allInProgress.isEmpty() && allCompleted.isEmpty()
        binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE

        binding.tvSectionInProgress.visibility = if (allInProgress.isNotEmpty()) View.VISIBLE else View.GONE
        binding.tvSectionCompleted.visibility = if (allCompleted.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun showSections(filter: String) {
        when (filter) {
            "all" -> {
                binding.rvInProgress.visibility = View.VISIBLE
                binding.rvCompleted.visibility = View.VISIBLE
                binding.tvSectionInProgress.visibility = if (allInProgress.isNotEmpty()) View.VISIBLE else View.GONE
                binding.tvSectionCompleted.visibility = if (allCompleted.isNotEmpty()) View.VISIBLE else View.GONE
            }
            "in_progress" -> {
                binding.rvInProgress.visibility = View.VISIBLE
                binding.rvCompleted.visibility = View.GONE
                binding.tvSectionInProgress.visibility = if (allInProgress.isNotEmpty()) View.VISIBLE else View.GONE
                binding.tvSectionCompleted.visibility = View.GONE
            }
            "completed" -> {
                binding.rvInProgress.visibility = View.GONE
                binding.rvCompleted.visibility = View.VISIBLE
                binding.tvSectionInProgress.visibility = View.GONE
                binding.tvSectionCompleted.visibility = if (allCompleted.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun navigateToCourse(courseId: String) {
        val intent = Intent(this, CoursePlayerActivity::class.java)
        intent.putExtra("COURSE_ID", courseId)
        startActivity(intent)
    }
}

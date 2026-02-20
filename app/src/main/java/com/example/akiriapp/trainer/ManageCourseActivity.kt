package com.example.akiriapp.trainer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akiriapp.adapter.LessonAdapter
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.data.repository.CourseRepository
import com.example.akiriapp.data.repository.LessonRepository
import com.example.akiriapp.databinding.ActivityManageCourseBinding
import kotlinx.coroutines.launch

class ManageCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageCourseBinding
    private val courseRepository = CourseRepository()
    private val lessonRepository = LessonRepository()
    private lateinit var lessonAdapter: LessonAdapter
    private var courseId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        if (courseId.isEmpty()) {
            Toast.makeText(this, "Erreur: ID du cours manquant", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        lessonAdapter = LessonAdapter { lesson ->
            Toast.makeText(this, "Édition prochainement...", Toast.LENGTH_SHORT).show()
        }
        binding.rvLessons.apply {
            layoutManager = LinearLayoutManager(this@ManageCourseActivity)
            adapter = lessonAdapter
        }
    }

    private fun setupButtons() {
        binding.btnAddLesson.setOnClickListener {
            val intent = Intent(this, AddLessonActivity::class.java)
            intent.putExtra("COURSE_ID", courseId)
            startActivity(intent)
        }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmptyState.visibility = View.GONE

        lifecycleScope.launch {
            // Load course details
            courseRepository.getCourseById(courseId).onSuccess { course ->
                binding.tvCourseTitle.text = course.title
                binding.tvCourseStats.text = "${course.enrollmentCount} Inscrits • ${course.lessonCount} Leçons"
            }.onFailure { e ->
                Toast.makeText(this@ManageCourseActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            // Load lessons
            lessonRepository.getLessonsForCourse(courseId).onSuccess { lessons ->
                binding.progressBar.visibility = View.GONE
                if (lessons.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvLessons.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvLessons.visibility = View.VISIBLE
                    lessonAdapter.submitList(lessons)
                }
            }.onFailure { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ManageCourseActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}

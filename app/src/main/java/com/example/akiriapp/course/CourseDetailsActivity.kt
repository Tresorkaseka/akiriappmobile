package com.example.akiriapp.course

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.example.akiriapp.R
import com.example.akiriapp.adapter.LessonAdapter
import com.example.akiriapp.data.repository.AuthRepository
import com.example.akiriapp.data.repository.CourseRepository
import com.example.akiriapp.data.repository.EnrollmentRepository
import com.example.akiriapp.data.repository.LessonRepository
import com.example.akiriapp.databinding.ActivityCourseDetailsBinding
import kotlinx.coroutines.launch

class CourseDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseDetailsBinding
    private val authRepository = AuthRepository()
    private val courseRepository = CourseRepository()
    private val enrollmentRepository = EnrollmentRepository()
    private val lessonRepository = LessonRepository()
    private val lessonAdapter = LessonAdapter { lesson ->
        // Could play specific lesson if enrolled, else prompt.
    }
    
    private var courseId: String = ""
    private var isEnrolled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        
        setupToolbar()
        setupRecyclerView()
        setupTabs()
        setupButtons()
        loadCourseData()
    }
    
    private fun setupRecyclerView() {
        binding.rvProgram.apply {
            layoutManager = LinearLayoutManager(this@CourseDetailsActivity)
            adapter = lessonAdapter
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showAboutContent()
                    1 -> showProgramContent()
                    2 -> showReviewsContent()
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun setupButtons() {
        binding.btnPreview.setOnClickListener {
            Toast.makeText(this, "Aperçu du cours...", Toast.LENGTH_SHORT).show()
        }

        binding.btnEnroll.setOnClickListener {
            if (isEnrolled) {
                // Navigate to continue course (Player)
                val intent = android.content.Intent(this, com.example.akiriapp.learning.CoursePlayerActivity::class.java)
                startActivity(intent)
            } else {
                enrollInCourse()
            }
        }
    }

    private fun loadCourseData() {
        if (courseId.isEmpty()) {
            // Show placeholder data if no course ID
            return
        }

        lifecycleScope.launch {
            // Load course details
            courseRepository.getCourseById(courseId).onSuccess { course ->
                binding.collapsingToolbar.title = course.title
                binding.tvCourseTitle.text = course.title
                binding.chipCategory.text = course.category
                binding.ratingBar.rating = course.rating
                binding.tvRating.text = String.format("%.1f", course.rating)
                binding.tvInstructorName.text = course.instructorName
                binding.tvDescription.text = course.description
                
                // Load Cover Image
                if (!course.thumbnailUrl.isNullOrEmpty()) {
                    Glide.with(this@CourseDetailsActivity)
                        .load(course.thumbnailUrl)
                        .centerCrop()
                        .placeholder(R.drawable.gradient_course_hero)
                        .error(R.drawable.gradient_course_hero)
                        .into(binding.ivCourseHero)
                }
                
                // Update enroll button with price
                binding.btnEnroll.text = getString(R.string.course_enroll_price, course.price.toString())
            }.onFailure { e ->
                Toast.makeText(this@CourseDetailsActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            // Check if user is enrolled
            val userId = authRepository.currentUser?.uid
            if (userId != null) {
                enrollmentRepository.isEnrolled(userId, courseId).onSuccess { enrolled ->
                    isEnrolled = enrolled
                    if (enrolled) {
                        binding.btnEnroll.text = getString(R.string.btn_continue)
                    }
                }
            }

            // Load and display lessons for Program tab
            lessonRepository.getLessonsForCourse(courseId).onSuccess { lessons ->
                if (lessons.isEmpty()) {
                    binding.tvProgramEmpty.visibility = View.VISIBLE
                    binding.rvProgram.visibility = View.GONE
                } else {
                    binding.tvProgramEmpty.visibility = View.GONE
                    binding.rvProgram.visibility = View.VISIBLE
                    lessonAdapter.submitList(lessons)
                }
            }
        }
    }

    private fun enrollInCourse() {
        val userId = authRepository.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            courseRepository.getCourseById(courseId).onSuccess { course ->
                enrollmentRepository.enrollInCourse(
                    userId = userId,
                    courseId = courseId,
                    courseTitle = course.title,
                    courseThumbnailUrl = course.thumbnailUrl
                ).onSuccess {
                    Toast.makeText(this@CourseDetailsActivity, "Inscription réussie!", Toast.LENGTH_SHORT).show()
                    isEnrolled = true
                    binding.btnEnroll.text = getString(R.string.btn_continue)
                    
                    // Launch player immediately
                    val intent = android.content.Intent(this@CourseDetailsActivity, com.example.akiriapp.learning.CoursePlayerActivity::class.java)
                    startActivity(intent)
                }.onFailure { e ->
                    Toast.makeText(this@CourseDetailsActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAboutContent() {
        binding.contentAbout.visibility = View.VISIBLE
        binding.contentProgram.visibility = View.GONE
    }

    private fun showProgramContent() {
        binding.contentAbout.visibility = View.GONE
        binding.contentProgram.visibility = View.VISIBLE
    }

    private fun showReviewsContent() {
        binding.contentAbout.visibility = View.GONE
        binding.contentProgram.visibility = View.GONE
        // TODO: Show reviews RecyclerView
    }
}

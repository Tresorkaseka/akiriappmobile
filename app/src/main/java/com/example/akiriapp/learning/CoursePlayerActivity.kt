package com.example.akiriapp.learning

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.data.model.Lesson
import com.example.akiriapp.data.repository.LessonRepository
import com.example.akiriapp.databinding.ActivityCoursePlayerBinding
import kotlinx.coroutines.launch

class CoursePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoursePlayerBinding
    private val lessonRepository = LessonRepository()
    private var lessonIndex = 0
    private var lessons: List<Lesson> = emptyList()
    private var courseId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoursePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("COURSE_ID") ?: ""

        setupControls()
        loadLessons()
    }

    private fun setupControls() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            if (lessons.isNotEmpty() && lessonIndex < lessons.size - 1) {
                lessonIndex++
                updateUI()
            } else {
                Toast.makeText(this, "Cours terminé ! Félicitations !", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnPrevious.setOnClickListener {
            if (lessonIndex > 0) {
                lessonIndex--
                updateUI()
            }
        }

        binding.playerView.setOnClickListener {
            playCurrentLesson()
        }
    }

    private fun loadLessons() {
        if (courseId.isEmpty()) {
            Toast.makeText(this, "Erreur de chargement du cours", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            lessonRepository.getLessonsForCourse(courseId).onSuccess { fetchedLessons ->
                lessons = fetchedLessons
                if (lessons.isNotEmpty()) {
                    lessonIndex = 0
                    updateUI()
                } else {
                    Toast.makeText(this@CoursePlayerActivity, "Ce cours ne contient aucune leçon pour le moment.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }.onFailure {
                Toast.makeText(this@CoursePlayerActivity, "Erreur de chargement des leçons", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun playCurrentLesson() {
        if (lessons.isEmpty()) return

        val currentLesson = lessons[lessonIndex]
        val url = currentLesson.contentUrl

        if (!url.isNullOrEmpty()) {
            try {
                // Try to open in YouTube app first, then fallback to browser
                val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                youtubeIntent.setPackage("com.google.android.youtube")
                if (youtubeIntent.resolveActivity(packageManager) != null) {
                    startActivity(youtubeIntent)
                } else {
                    // Fallback to browser
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Impossible d'ouvrir la vidéo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "URL vidéo manquante pour cette leçon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        if (lessons.isEmpty()) return

        val currentLesson = lessons[lessonIndex]
        binding.tvLessonTitle.text = "Leçon ${lessonIndex + 1} : ${currentLesson.title}"
        binding.btnPrevious.isEnabled = lessonIndex > 0
        binding.btnNext.text = if (lessonIndex == lessons.size - 1) "Terminer" else "Suivant ›"

        val url = currentLesson.contentUrl
        if (!url.isNullOrEmpty() && (url.contains("youtube.com") || url.contains("youtu.be"))) {
            binding.playerView.visibility = View.GONE
            binding.youtubeOverlay.visibility = View.VISIBLE
            binding.btnOpenYoutube.setOnClickListener {
                playCurrentLesson()
            }
        } else {
            binding.youtubeOverlay.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE
        }
    }
}

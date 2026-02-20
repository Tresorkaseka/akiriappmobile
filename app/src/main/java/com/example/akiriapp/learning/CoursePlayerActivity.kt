package com.example.akiriapp.learning

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
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
    
    // ExoPlayer instance
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoursePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        
        setupControls()
        loadLessons()
    }
    
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
    }
    
    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onStart() {
        super.onStart()
        if (player == null) {
            initializePlayer()
            if (lessons.isNotEmpty()) {
                playCurrentLesson()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
    
    private fun setupControls() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.btnNext.setOnClickListener {
            if (lessons.isNotEmpty() && lessonIndex < lessons.size - 1) {
                lessonIndex++
                updateUI()
                playCurrentLesson()
            } else {
                Toast.makeText(this, "Cours terminé ! Félicitations !", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        
        binding.btnPrevious.setOnClickListener {
            if (lessonIndex > 0) {
                lessonIndex--
                updateUI()
                playCurrentLesson()
            }
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
                    if (player != null) {
                        playCurrentLesson()
                    }
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
        if (lessons.isEmpty() || player == null) return
        
        val currentLesson = lessons[lessonIndex]
        val url = currentLesson.contentUrl
        
        if (!url.isNullOrEmpty() && currentLesson.isVideo()) {
            val mediaItem = MediaItem.fromUri(url)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        } else {
            // Either no URL or not a video type. Just stop player.
            player?.stop()
            player?.clearMediaItems()
            if (currentLesson.isVideo()) {
                 Toast.makeText(this, "URL vidéo manquante", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateUI() {
        if (lessons.isEmpty()) return
        
        val currentLesson = lessons[lessonIndex]
        binding.tvLessonTitle.text = "Leçon ${lessonIndex + 1} : ${currentLesson.title}"
        binding.btnPrevious.isEnabled = lessonIndex > 0
        binding.btnNext.text = if (lessonIndex == lessons.size - 1) "Terminer" else "Suivant"
    }
}

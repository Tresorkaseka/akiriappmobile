package com.example.akiriapp.learning

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.akiriapp.databinding.ActivityCoursePlayerBinding

class CoursePlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoursePlayerBinding
    private var lessonIndex = 0
    private val totalLessons = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoursePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupControls()
        updateUI()
    }
    
    private fun setupControls() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.btnNext.setOnClickListener {
            if (lessonIndex < totalLessons - 1) {
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
    }
    
    private fun updateUI() {
        binding.tvLessonTitle.text = "Leçon ${lessonIndex + 1} : Titre de la leçon"
        binding.btnPrevious.isEnabled = lessonIndex > 0
        binding.btnNext.text = if (lessonIndex == totalLessons - 1) "Terminer" else "Suivant"
    }
}

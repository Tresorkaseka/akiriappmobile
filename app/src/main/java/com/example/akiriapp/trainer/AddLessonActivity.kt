package com.example.akiriapp.trainer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.data.model.Lesson
import com.example.akiriapp.data.repository.LessonRepository
import com.example.akiriapp.data.repository.StorageRepository
import com.example.akiriapp.databinding.ActivityAddLessonBinding
import kotlinx.coroutines.launch

class AddLessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLessonBinding
    private val lessonRepository = LessonRepository()
    private val storageRepository = StorageRepository()
    private var courseId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseId = intent.getStringExtra("COURSE_ID") ?: ""

        setupToolbar()
        setupDropdown()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupDropdown() {
        val types = listOf("video", "text", "quiz")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types)
        binding.actType.setAdapter(adapter)
        binding.actType.setText("video", false)
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val type = binding.actType.text.toString()
            val duration = binding.etDuration.text.toString()
            val videoUrl = binding.etVideoUrl.text.toString()

            if (title.isBlank() || description.isBlank()) {
                Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (type == "video" && videoUrl.isBlank()) {
                Toast.makeText(this, "Veuillez fournir un lien vidéo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveLesson(title, description, type, duration, videoUrl)
        }
    }

    private fun saveLesson(title: String, description: String, type: String, duration: String, videoUrl: String) {
        binding.btnSave.isEnabled = false
        
        lifecycleScope.launch {
            // Fetch current lesson count to set 'order'
            var currentOrder = 1
            lessonRepository.getLessonsForCourse(courseId).onSuccess { lessons ->
                currentOrder = lessons.size + 1
            }

            val lesson = Lesson(
                courseId = courseId,
                title = title,
                description = description,
                contentType = type,
                contentUrl = videoUrl, // Now stores the YouTube URL directly
                duration = duration,
                order = currentOrder
            )

            lessonRepository.addLesson(lesson).onSuccess {
                Toast.makeText(this@AddLessonActivity, "Leçon ajoutée !", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { e ->
                handleError("Erreur: ${e.message}")
            }
        }
    }
    
    private fun handleError(message: String) {
        binding.btnSave.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

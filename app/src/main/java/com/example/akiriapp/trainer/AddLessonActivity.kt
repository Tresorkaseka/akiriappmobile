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
    private var selectedVideoUri: Uri? = null

    // Activity Result Launcher for picking a video
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
            val fileName = getFileName(uri)
            binding.tvSelectedVideoName.text = fileName ?: "Vidéo sélectionnée"
        } else {
            Toast.makeText(this, "Aucune vidéo sélectionnée", Toast.LENGTH_SHORT).show()
        }
    }

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
        binding.btnSelectVideo.setOnClickListener {
            // Launch the photo picker to let the user choose only videos
            pickMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val type = binding.actType.text.toString()
            val duration = binding.etDuration.text.toString()

            if (title.isBlank() || description.isBlank()) {
                Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (type == "video" && selectedVideoUri == null) {
                Toast.makeText(this, "Veuillez sélectionner une vidéo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveLesson(title, description, type, duration)
        }
    }

    private fun saveLesson(title: String, description: String, type: String, duration: String) {
        binding.btnSave.isEnabled = false
        binding.btnSelectVideo.isEnabled = false
        binding.pbUpload.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            // 1. Upload Video if selected
            var contentUrl: String? = null
            selectedVideoUri?.let { uri ->
                binding.tvSelectedVideoName.text = "Upload en cours..."
                storageRepository.uploadLessonVideo(uri).onSuccess { url ->
                    contentUrl = url
                }.onFailure { e ->
                    handleError("Erreur upload vidéo: ${e.message}")
                    return@launch
                }
            }

            // 2. Fetch current lesson count to set 'order'
            var currentOrder = 1
            lessonRepository.getLessonsForCourse(courseId).onSuccess { lessons ->
                currentOrder = lessons.size + 1
            }

            val lesson = Lesson(
                courseId = courseId,
                title = title,
                description = description,
                contentType = type,
                contentUrl = contentUrl, // Now stores the Firebase Storage URL
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
        binding.pbUpload.visibility = View.GONE
        binding.btnSave.isEnabled = true
        binding.btnSelectVideo.isEnabled = true
        binding.tvSelectedVideoName.text = "Erreur, veuillez réessayer"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = cursor.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }
}

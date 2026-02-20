package com.example.akiriapp.trainer

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.data.repository.AuthRepository
import com.example.akiriapp.data.repository.CourseRepository
import com.example.akiriapp.data.repository.StorageRepository
import com.example.akiriapp.databinding.ActivityCreateCourseBinding
import kotlinx.coroutines.launch

class CreateCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCourseBinding
    private val authRepository = AuthRepository()
    private val courseRepository = CourseRepository()
    private val storageRepository = StorageRepository()
    
    private var selectedImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupImagePicker()
        setupCreateButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupImagePicker() {
        binding.btnSelectImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupCreateButton() {
        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val priceStr = binding.etPrice.text.toString()
            val category = binding.actCategory.text.toString()

            if (title.isBlank() || description.isBlank() || priceStr.isBlank() || category.isBlank()) {
                Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (selectedImageUri == null) {
                Toast.makeText(this, "Veuillez choisir une image de couverture", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toIntOrNull() ?: 0
            
            startCourseCreation(title, description, price, category)
        }
    }

    private fun startCourseCreation(title: String, description: String, price: Int, category: String) {
        val userId = authRepository.currentUser?.uid ?: return
        
        binding.btnCreate.isEnabled = false
        Toast.makeText(this, "Création du cours en cours...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            // 1. Upload Image first if selected
            var thumbnailUrl: String? = null
            selectedImageUri?.let { uri ->
                storageRepository.uploadCourseThumbnail(uri).onSuccess { url ->
                    thumbnailUrl = url
                }.onFailure { e ->
                    Toast.makeText(this@CreateCourseActivity, "Erreur upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.btnCreate.isEnabled = true
                    return@launch
                }
            }

            // 2. Create Course in Firestore
            authRepository.getCurrentUserProfile().onSuccess { user ->
                val newCourse = Course(
                    title = title,
                    description = description,
                    price = price,
                    instructorId = userId,
                    instructorName = user.fullName,
                    category = category,
                    thumbnailUrl = thumbnailUrl,
                    duration = "0h 0min",
                    lessonCount = 0
                )
                
                courseRepository.createCourse(newCourse).onSuccess { courseId ->
                    Toast.makeText(this@CreateCourseActivity, "Cours créé avec succès !", Toast.LENGTH_LONG).show()
                    val intent = android.content.Intent(this@CreateCourseActivity, ManageCourseActivity::class.java).apply {
                        putExtra("COURSE_ID", courseId)
                    }
                    startActivity(intent)
                    finish()
                }.onFailure { e ->
                    Toast.makeText(this@CreateCourseActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.btnCreate.isEnabled = true
                }
            }.onFailure { e ->
                Toast.makeText(this@CreateCourseActivity, "Erreur profil: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnCreate.isEnabled = true
            }
        }
    }
}

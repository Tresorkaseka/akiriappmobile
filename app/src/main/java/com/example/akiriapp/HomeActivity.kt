package com.example.akiriapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akiriapp.adapter.CategoryAdapter
import com.example.akiriapp.adapter.CourseAdapter
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.databinding.ActivityHomeBinding
import com.example.akiriapp.course.CourseDetailsActivity
import com.example.akiriapp.forum.ForumActivity
import com.example.akiriapp.learning.MyLearningActivity
import com.example.akiriapp.settings.SettingsActivity

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.akiriapp.data.repository.CourseRepository
import com.example.akiriapp.data.repository.StorageRepository
import android.util.Log
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import com.example.akiriapp.R
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularCourseAdapter: CourseAdapter
    private lateinit var recommendedCourseAdapter: CourseAdapter
    private val courseRepository = CourseRepository()
    
    // Store full lists for search filtering
    private var allPopularCourses: List<Course> = emptyList()
    private var allRecommendedCourses: List<Course> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupBottomNavigation()
        setupSearch()
        loadDummyData()
        
        binding.imgLogo.setOnClickListener { 
            // Refresh or scroll to top
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterCourses(s.toString())
            }
        })
    }

    private fun filterCourses(query: String) {
        val lowerCaseQuery = query.lowercase()
        
        val filteredPopular = if (lowerCaseQuery.isEmpty()) {
            allPopularCourses
        } else {
            allPopularCourses.filter {
                it.title.lowercase().contains(lowerCaseQuery) || 
                it.category.lowercase().contains(lowerCaseQuery)
            }
        }
        popularCourseAdapter.submitList(filteredPopular)

        val filteredRecommended = if (lowerCaseQuery.isEmpty()) {
            allRecommendedCourses
        } else {
            allRecommendedCourses.filter {
                it.title.lowercase().contains(lowerCaseQuery) || 
                it.category.lowercase().contains(lowerCaseQuery)
            }
        }
        recommendedCourseAdapter.submitList(filteredRecommended)
    }

    private fun setupAdapters() {
        // Categories
        val categories = listOf("Tout", "Programmation", "Design", "Business", "Marketing", "Finance", "Santé")
        categoryAdapter = CategoryAdapter(categories) { category ->
            Toast.makeText(this, "Selected: $category", Toast.LENGTH_SHORT).show()
            // Filter logic would go here
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Popular Courses
        popularCourseAdapter = CourseAdapter { course ->
            openCourseDetails(course)
        }
        binding.rvPopular.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularCourseAdapter
        }

        // Recommended Courses
        recommendedCourseAdapter = CourseAdapter { course ->
            openCourseDetails(course)
        }
        binding.rvRecommended.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedCourseAdapter
        }
    }

    private fun openCourseDetails(course: Course) {
        val intent = Intent(this, CourseDetailsActivity::class.java)
        intent.putExtra("COURSE_ID", course.id)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_forum -> {
                    startActivity(Intent(this, ForumActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_my_learning -> {
                    startActivity(Intent(this, MyLearningActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadDummyData() {
        lifecycleScope.launch {
            // Load Popular
            courseRepository.getPopularCourses(5).onSuccess { courses ->
                if (courses.isNotEmpty()) {
                    allPopularCourses = courses
                    popularCourseAdapter.submitList(courses)
                } else {
                    Log.d("HomeActivity", "No popular courses found, loading recents")
                    // If no popular courses, fall back to recent ones to ensure learners see something
                    courseRepository.getCourses(limit = 5).onSuccess { recentCourses ->
                    	 allPopularCourses = recentCourses
                         popularCourseAdapter.submitList(recentCourses)
                    }
                }
            }.onFailure { e ->
                Log.e("HomeActivity", "Error loading popular: ${e.message}")
            }

            // Load Recommended
            courseRepository.getCourses(limit = 10).onSuccess { courses ->
                if (courses.isNotEmpty()) {
                    allRecommendedCourses = courses
                    recommendedCourseAdapter.submitList(courses)
                } else {
                    Log.d("HomeActivity", "No recommended courses found")
                    allRecommendedCourses = emptyList()
                    recommendedCourseAdapter.submitList(emptyList())
                }
            }.onFailure { e ->
                Log.e("HomeActivity", "Error loading recommended: ${e.message}")
            }
        }
    }

    private fun uploadMissingThumbnails() {
        lifecycleScope.launch {
            val storageRepo = StorageRepository()
            
            courseRepository.getCourses(limit = 100).onSuccess { courses ->
                courses.filter { it.thumbnailUrl == null || it.thumbnailUrl.trim().isEmpty() }.forEach { course ->
                    val resId = when (course.title) {
                        "Introduction à l'IA" -> R.drawable.course_ia
                        "Design UX/UI Avancé" -> R.drawable.course_uxui
                        "Marketing Digital" -> R.drawable.course_marketing
                        "Développement Web Fullstack" -> R.drawable.course_webdev
                        "Data Science pour Débutants" -> R.drawable.course_data_science
                        "Anglais de Communication" -> R.drawable.course_english
                        "Masterclass Photoshop" -> R.drawable.course_photoshop
                        "React Native Pro" -> R.drawable.course_react_native
                        else -> R.drawable.generated_course_programmation
                    }
                    
                    try {
                        val bitmap = android.graphics.BitmapFactory.decodeResource(resources, resId)
                        val baos = java.io.ByteArrayOutputStream()
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
                        val data = baos.toByteArray()
                        
                        Log.d("HomeActivity", "Uploading thumbnail for course ${course.title}")
                        storageRepo.uploadCourseThumbnail(data).onSuccess { downloadUrl ->
                            courseRepository.updateCourse(course.id, mapOf("thumbnailUrl" to downloadUrl))
                            Log.d("HomeActivity", "Successfully updated ${course.title} with new cover.")
                        }.onFailure { e ->
                            Log.e("HomeActivity", "Failed to upload for ${course.title}: ${e.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("HomeActivity", "Error processing image for ${course.title}: ${e.message}")
                    }
                }
            }
        }
    }

    private fun injectNewCoursesAndCleanup() {
        lifecycleScope.launch {
            courseRepository.getCourses(limit = 100).onSuccess { courses ->
                val db = FirebaseFirestore.getInstance()
                
                // 1. Cleanup duplicates of "Entrepreneuriat"
                val entrepreneuriatCourses = courses.filter { it.title.contains("Entrepreneuriat", ignoreCase = true) }
                if (entrepreneuriatCourses.size > 1) {
                    for (i in 1 until entrepreneuriatCourses.size) {
                        db.collection("courses").document(entrepreneuriatCourses[i].id).delete()
                        Log.d("HomeActivity", "Deleted duplicate course: ${entrepreneuriatCourses[i].id}")
                    }
                }
                
                // 2. Add 4 new courses if they do not exist
                val newCourseTitles = listOf("Data Science pour Débutants", "Anglais de Communication", "Masterclass Photoshop", "React Native Pro")
                for (title in newCourseTitles) {
                    if (courses.none { it.title == title }) {
                        val category = when (title) {
                            "Data Science pour Débutants" -> "Programmation"
                            "Anglais de Communication" -> "Langues"
                            "Masterclass Photoshop" -> "Design"
                            "React Native Pro" -> "Programmation"
                            else -> "Business"
                        }
                        
                        val newCourse = Course(
                            title = title,
                            description = "Un cours complet sur $title pour maîtriser les compétences essentielles et avancer dans votre carrière. Ce programme est conçu pour les débutants comme pour les confirmés.",
                            price = 5000,
                            instructorId = "system",
                            instructorName = "Akiriapp Academy",
                            category = category,
                            duration = "5h 30min",
                            lessonCount = 0
                        )
                        courseRepository.createCourse(newCourse).onSuccess {
                            Log.d("HomeActivity", "Added new course: $title")
                        }
                    }
                }

                // 3. Ensure all courses have at least 3 lessons and 3 reviews, and a long description
                for (course in courses) {
                    // Update empty descriptions
                    if (course.description.isBlank() || course.description.length < 20) {
                        db.collection("courses").document(course.id).update(
                            "description", "Découvrez les secrets de ${course.title} à travers ce cours intensif. Vous apprendrez la théorie et la pratique grâce à des modules interactifs conçus par des experts de l'industrie."
                        )
                    }

                    // Seeding Lessons
                    db.collection("lessons").whereEqualTo("courseId", course.id).get().addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            val dummyLessons = listOf(
                                mapOf("courseId" to course.id, "title" to "Introduction à ${course.title}", "order" to 1, "contentType" to "video", "contentUrl" to "https://www.youtube.com/watch?v=lFguWR9uMD0", "duration" to "10:00", "description" to "Découvrez les bases et le contexte général de ce module passionnant.", "createdAt" to System.currentTimeMillis()),
                                mapOf("courseId" to course.id, "title" to "Concepts Avancés et Pratique", "order" to 2, "contentType" to "video", "contentUrl" to "https://www.youtube.com/watch?v=2JuOogXddrc", "duration" to "25:30", "description" to "Plongez dans les détails techniques et les études de cas concrètes.", "createdAt" to System.currentTimeMillis() + 1000),
                                mapOf("courseId" to course.id, "title" to "Conclusion et Projet Final", "order" to 3, "contentType" to "video", "contentUrl" to "https://www.youtube.com/watch?v=E3YPG4OqZT4", "duration" to "15:45", "description" to "Testez vos connaissances et préparez vous pour le monde professionnel.", "createdAt" to System.currentTimeMillis() + 2000)
                            )
                            dummyLessons.forEach { lesson ->
                                db.collection("lessons").add(lesson)
                            }
                            db.collection("courses").document(course.id).update("lessonCount", 3)
                            Log.d("HomeActivity", "Added 3 dummy lessons to ${course.title}")
                        }
                    }

                    // Seeding Reviews
                    db.collection("reviews").whereEqualTo("courseId", course.id).get().addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            val dummyReviews = listOf(
                                mapOf("courseId" to course.id, "userId" to "user1", "userName" to "Alice M.", "rating" to 5f, "comment" to "Ce cours est exceptionnel ! Le formateur explique vraiment bien. Je le recommande fortement.", "createdAt" to System.currentTimeMillis() - 86400000), // 1 day ago
                                mapOf("courseId" to course.id, "userId" to "user2", "userName" to "Jean K.", "rating" to 4f, "comment" to "Le contenu est très riche et pertinent, bien qu'un peu rapide par moments.", "createdAt" to System.currentTimeMillis() - 172800000), // 2 days ago
                                mapOf("courseId" to course.id, "userId" to "user3", "userName" to "Marc D.", "rating" to 5f, "comment" to "M'a énormément aidé pour mes projets personnels. 5 étoiles largement méritées.", "createdAt" to System.currentTimeMillis() - 259200000) // 3 days ago
                            )
                            dummyReviews.forEach { review ->
                                db.collection("reviews").add(review)
                            }
                            db.collection("courses").document(course.id).update(
                                mapOf("reviewCount" to 3, "rating" to 4.6f)
                            )
                            Log.d("HomeActivity", "Added 3 dummy reviews to ${course.title}")
                        }
                    }
                }
            }
        }
    }
}

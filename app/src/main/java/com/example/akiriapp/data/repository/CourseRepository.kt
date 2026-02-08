package com.example.akiriapp.data.repository

import com.example.akiriapp.data.model.Course
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling Course data operations with Firestore.
 */
class CourseRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val coursesCollection = firestore.collection("courses")

    /**
     * Get all courses, optionally filtered by category.
     */
    suspend fun getCourses(category: String? = null, limit: Long = 20): Result<List<Course>> {
        return try {
            var query: Query = coursesCollection.orderBy("createdAt", Query.Direction.DESCENDING)
            
            if (!category.isNullOrEmpty()) {
                query = query.whereEqualTo("category", category)
            }
            
            val snapshot = query.limit(limit).get().await()
            val courses = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Course::class.java)?.copy(id = doc.id)
            }
            Result.success(courses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get a single course by ID.
     */
    suspend fun getCourseById(courseId: String): Result<Course> {
        return try {
            val doc = coursesCollection.document(courseId).get().await()
            val course = doc.toObject(Course::class.java)?.copy(id = doc.id)
            if (course != null) {
                Result.success(course)
            } else {
                Result.failure(Exception("Cours non trouvé"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get courses by instructor ID (for trainer dashboard).
     */
    suspend fun getCoursesByInstructor(instructorId: String): Result<List<Course>> {
        return try {
            val snapshot = coursesCollection
                .whereEqualTo("instructorId", instructorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()
            
            val courses = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Course::class.java)?.copy(id = doc.id)
            }
            Result.success(courses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a new course (for trainers).
     */
    suspend fun createCourse(course: Course): Result<String> {
        return try {
            val docRef = coursesCollection.add(course).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an existing course.
     */
    suspend fun updateCourse(courseId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["updatedAt"] = System.currentTimeMillis()
            coursesCollection.document(courseId).update(updatesWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search courses by title.
     */
    suspend fun searchCourses(query: String): Result<List<Course>> {
        return try {
            // Firestore doesn't support full-text search, so we use a prefix match
            val snapshot = coursesCollection
                .orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(10)
                .get().await()
            
            val courses = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Course::class.java)?.copy(id = doc.id)
            }
            Result.success(courses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get popular courses (by enrollment count).
     */
    suspend fun getPopularCourses(limit: Long = 10): Result<List<Course>> {
        return try {
            val snapshot = coursesCollection
                .orderBy("enrollmentCount", Query.Direction.DESCENDING)
                .limit(limit)
                .get().await()
            
            val courses = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Course::class.java)?.copy(id = doc.id)
            }
            Result.success(courses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get available categories.
     */
    fun getCategories(): List<String> {
        return listOf(
            "Programmation",
            "Design",
            "Marketing",
            "Business",
            "Langues",
            "Sciences"
        )
    }
}

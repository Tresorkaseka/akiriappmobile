package com.example.akiriapp.data.repository

import com.example.akiriapp.data.model.Enrollment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling Enrollment data operations with Firestore.
 */
class EnrollmentRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val enrollmentsCollection = firestore.collection("enrollments")
    private val coursesCollection = firestore.collection("courses")

    /**
     * Enroll a user in a course.
     */
    suspend fun enrollInCourse(
        userId: String,
        courseId: String,
        courseTitle: String,
        courseThumbnailUrl: String?
    ): Result<String> {
        return try {
            // Check if already enrolled
            val existing = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .get().await()
            
            if (!existing.isEmpty) {
                return Result.failure(Exception("Déjà inscrit à ce cours"))
            }
            
            val enrollment = Enrollment(
                userId = userId,
                courseId = courseId,
                courseTitle = courseTitle,
                courseThumbnailUrl = courseThumbnailUrl,
                progress = 0,
                status = "in_progress"
            )
            
            val docRef = enrollmentsCollection.add(enrollment).await()
            
            // Increment enrollment count on the course
            coursesCollection.document(courseId)
                .update("enrollmentCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all enrollments for a user.
     */
    suspend fun getUserEnrollments(userId: String): Result<List<Enrollment>> {
        return try {
            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .get().await()
            
            val enrollments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Enrollment::class.java)?.copy(id = doc.id)
            }.sortedByDescending { it.lastAccessedAt }
            Result.success(enrollments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get in-progress enrollments for a user.
     */
    suspend fun getInProgressEnrollments(userId: String): Result<List<Enrollment>> {
        return try {
            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "in_progress")
                .get().await()
            
            val enrollments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Enrollment::class.java)?.copy(id = doc.id)
            }.sortedByDescending { it.lastAccessedAt }
            Result.success(enrollments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get completed enrollments for a user.
     */
    suspend fun getCompletedEnrollments(userId: String): Result<List<Enrollment>> {
        return try {
            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "completed")
                .get().await()
            
            val enrollments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Enrollment::class.java)?.copy(id = doc.id)
            }
            Result.success(enrollments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update progress for an enrollment.
     */
    suspend fun updateProgress(
        enrollmentId: String,
        progress: Int,
        completedLessonId: String? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "progress" to progress,
                "lastAccessedAt" to System.currentTimeMillis()
            )
            
            if (progress >= 100) {
                updates["status"] = "completed"
            }
            
            if (completedLessonId != null) {
                updates["completedLessons"] = com.google.firebase.firestore.FieldValue.arrayUnion(completedLessonId)
            }
            
            enrollmentsCollection.document(enrollmentId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get the last accessed enrollment (for "Continue where you left off").
     */
    suspend fun getLastAccessedEnrollment(userId: String): Result<Enrollment?> {
        return try {
            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "in_progress")
                .get().await()
            
            val enrollment = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Enrollment::class.java)?.copy(id = doc.id)
            }.maxByOrNull { it.lastAccessedAt }
            Result.success(enrollment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if user is enrolled in a course.
     */
    suspend fun isEnrolled(userId: String, courseId: String): Result<Boolean> {
        return try {
            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .limit(1)
                .get().await()
            
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get enrollment stats for a user.
     */
    suspend fun getEnrollmentStats(userId: String): Result<Pair<Int, Int>> {
        return try {
            val allEnrollments = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .get().await()
            
            var inProgress = 0
            var completed = 0
            
            allEnrollments.documents.forEach { doc ->
                when (doc.getString("status")) {
                    "in_progress" -> inProgress++
                    "completed" -> completed++
                }
            }
            
            Result.success(Pair(inProgress, completed))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

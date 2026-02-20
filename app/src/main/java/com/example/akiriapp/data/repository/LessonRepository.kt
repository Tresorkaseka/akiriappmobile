package com.example.akiriapp.data.repository

import com.example.akiriapp.data.model.Lesson
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling Lesson data operations with Firestore.
 */
class LessonRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val lessonsCollection = firestore.collection("lessons")
    private val coursesCollection = firestore.collection("courses")

    /**
     * Get all lessons for a specific course, ordered by their 'order' index.
     */
    suspend fun getLessonsForCourse(courseId: String): Result<List<Lesson>> {
        return try {
            val snapshot = lessonsCollection
                .whereEqualTo("courseId", courseId)
                .orderBy("order", Query.Direction.ASCENDING)
                .get().await()
            
            val lessons = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Lesson::class.java)?.copy(id = doc.id)
            }
            Result.success(lessons)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add a new lesson to a course.
     * Also updates the lessonCount on the parent course document.
     */
    suspend fun addLesson(lesson: Lesson): Result<String> {
        return try {
            // Add the lesson
            val docRef = lessonsCollection.add(lesson).await()
            
            // Increment lesson count on the course
            coursesCollection.document(lesson.courseId)
                .update("lessonCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
                
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an existing lesson.
     */
    suspend fun updateLesson(lessonId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            lessonsCollection.document(lessonId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a lesson.
     * Also decrements the lessonCount on the parent course document.
     */
    suspend fun deleteLesson(lessonId: String, courseId: String): Result<Unit> {
        return try {
            // Delete the lesson
            lessonsCollection.document(lessonId).delete().await()
            
            // Decrement lesson count on the course
            coursesCollection.document(courseId)
                .update("lessonCount", com.google.firebase.firestore.FieldValue.increment(-1))
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

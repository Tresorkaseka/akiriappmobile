package com.example.akiriapp.data.model

/**
 * Represents a lesson within a course.
 * Stored in Firestore collection: lessons/{lessonId}
 */
data class Lesson(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val order: Int = 0, // Order within the course
    val contentType: String = "video", // "video", "text", "quiz"
    val contentUrl: String? = null,
    val duration: String = "", // e.g., "15:30" for video
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", 0, "video", null, "", "", 0L)
    
    fun isVideo(): Boolean = contentType == "video"
    fun isText(): Boolean = contentType == "text"
    fun isQuiz(): Boolean = contentType == "quiz"
}

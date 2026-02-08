package com.example.akiriapp.data.model

/**
 * Represents an enrollment record linking a user to a course.
 * Stored in Firestore collection: enrollments/{enrollmentId}
 */
data class Enrollment(
    val id: String = "",
    val userId: String = "",
    val courseId: String = "",
    val courseTitle: String = "", // Denormalized for faster queries
    val courseThumbnailUrl: String? = null,
    val progress: Int = 0, // 0-100 percentage
    val status: String = "in_progress", // "in_progress" or "completed"
    val completedLessons: List<String> = emptyList(), // List of lesson IDs
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val enrolledAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", "", null, 0, "in_progress", emptyList(), 0L, 0L)
    
    fun isCompleted(): Boolean = status == "completed"
    fun isInProgress(): Boolean = status == "in_progress"
}

package com.example.akiriapp.data.model

/**
 * Represents a user review for a course.
 * Stored in Firestore collection: reviews/{reviewId}
 */
data class Review(
    val id: String = "",
    val courseId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", "", 0f, "", 0L)
}

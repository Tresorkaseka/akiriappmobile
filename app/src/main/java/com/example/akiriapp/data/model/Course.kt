package com.example.akiriapp.data.model

/**
 * Represents a course in the Akiriapp system.
 * Stored in Firestore collection: courses/{courseId}
 */
data class Course(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int = 0, // Price in FC (Congolese Franc)
    val instructorId: String = "",
    val instructorName: String = "",
    val category: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val thumbnailUrl: String? = null,
    val duration: String = "", // e.g., "12h 30min"
    val lessonCount: Int = 0,
    val enrollmentCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val imageResId: Int? = null // For local dummy data
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", 0, "", "", "", 0f, 0, null, "", 0, 0, 0L, 0L, null)
    
    fun getFormattedPrice(): String = "$price FC"
    fun isFree(): Boolean = price == 0
}

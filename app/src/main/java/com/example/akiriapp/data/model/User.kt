package com.example.akiriapp.data.model

/**
 * Represents a user in the Akiriapp system.
 * Stored in Firestore collection: users/{userId}
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val role: String = "student", // "student" or "trainer"
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", "student", null, 0L)
    
    fun isTrainer(): Boolean = role == "trainer"
    fun isStudent(): Boolean = role == "student"
}

package com.example.akiriapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling Firebase Authentication and User Role management.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    /**
     * Signs up a new user with email and password, then stores their role in Firestore.
     * @param email User's email address.
     * @param password User's password.
     * @param fullName User's full name.
     * @param role User's role ("student" or "trainer").
     * @return Result containing FirebaseUser on success, or Exception on failure.
     */
    suspend fun signUp(email: String, password: String, fullName: String, role: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            
            // Store user profile with role in Firestore
            val userProfile = hashMapOf(
                "uid" to user.uid,
                "email" to email,
                "fullName" to fullName,
                "role" to role,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(user.uid).set(userProfile).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs in an existing user with email and password.
     * @param email User's email address.
     * @param password User's password.
     * @return Result containing FirebaseUser on success, or Exception on failure.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the user's role from Firestore.
     * @param uid User's unique ID.
     * @return The user's role as a String, or null if not found.
     */
    suspend fun getUserRole(uid: String): String? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.getString("role")
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        auth.signOut()
    }
}

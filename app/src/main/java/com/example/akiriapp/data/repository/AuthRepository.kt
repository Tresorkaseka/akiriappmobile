package com.example.akiriapp.data.repository

import com.example.akiriapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling Firebase Authentication and User management.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = currentUser != null

    /**
     * Signs up a new user with email and password, then stores their profile in Firestore.
     */
    suspend fun signUp(email: String, password: String, fullName: String, role: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Échec de la création du compte")
            
            val userProfile = User(
                uid = user.uid,
                email = email,
                fullName = fullName,
                role = role,
                createdAt = System.currentTimeMillis()
            )
            
            usersCollection.document(user.uid).set(userProfile).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs in an existing user with email and password.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Échec de la connexion")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get the current user's profile from Firestore.
     */
    suspend fun getCurrentUserProfile(): Result<User> {
        return try {
            val uid = currentUser?.uid ?: throw Exception("Non connecté")
            val doc = usersCollection.document(uid).get().await()
            val user = doc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Profil non trouvé"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get a user's profile by ID.
     */
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Utilisateur non trouvé"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the user's role from Firestore.
     */
    suspend fun getUserRole(uid: String): String? {
        return try {
            val document = usersCollection.document(uid).get().await()
            document.getString("role")
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Update user profile.
     */
    suspend fun updateProfile(updates: Map<String, Any>): Result<Unit> {
        return try {
            val uid = currentUser?.uid ?: throw Exception("Non connecté")
            usersCollection.document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

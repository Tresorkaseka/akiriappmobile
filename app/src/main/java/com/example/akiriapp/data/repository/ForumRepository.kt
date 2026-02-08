package com.example.akiriapp.data.repository

import com.example.akiriapp.data.model.ForumPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling Forum data operations with Firestore.
 */
class ForumRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("forum_posts")

    /**
     * Get all forum posts, optionally filtered by topic.
     */
    suspend fun getPosts(topic: String? = null, limit: Long = 20): Result<List<ForumPost>> {
        return try {
            var query: Query = postsCollection.orderBy("createdAt", Query.Direction.DESCENDING)
            
            if (!topic.isNullOrEmpty() && topic != "Tous") {
                query = query.whereEqualTo("topic", topic)
            }
            
            val snapshot = query.limit(limit).get().await()
            val posts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ForumPost::class.java)?.copy(id = doc.id)
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a new forum post.
     */
    suspend fun createPost(post: ForumPost): Result<String> {
        return try {
            val docRef = postsCollection.add(post).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Like a post.
     */
    suspend fun likePost(postId: String, userId: String): Result<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            
            firestore.runTransaction { transaction ->
                val post = transaction.get(postRef)
                val likedBy = post.get("likedBy") as? List<*> ?: emptyList<String>()
                
                if (!likedBy.contains(userId)) {
                    transaction.update(postRef, mapOf(
                        "likes" to com.google.firebase.firestore.FieldValue.increment(1),
                        "likedBy" to com.google.firebase.firestore.FieldValue.arrayUnion(userId)
                    ))
                }
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Unlike a post.
     */
    suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            
            firestore.runTransaction { transaction ->
                val post = transaction.get(postRef)
                val likedBy = post.get("likedBy") as? List<*> ?: emptyList<String>()
                
                if (likedBy.contains(userId)) {
                    transaction.update(postRef, mapOf(
                        "likes" to com.google.firebase.firestore.FieldValue.increment(-1),
                        "likedBy" to com.google.firebase.firestore.FieldValue.arrayRemove(userId)
                    ))
                }
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get posts by a specific user.
     */
    suspend fun getPostsByUser(userId: String): Result<List<ForumPost>> {
        return try {
            val snapshot = postsCollection
                .whereEqualTo("authorId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()
            
            val posts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ForumPost::class.java)?.copy(id = doc.id)
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get available topics.
     */
    fun getTopics(): List<String> {
        return listOf(
            "Tous",
            "Programmation",
            "Design",
            "Marketing",
            "Business",
            "Langues",
            "Aide"
        )
    }

    /**
     * Delete a post (only by author).
     */
    suspend fun deletePost(postId: String, userId: String): Result<Unit> {
        return try {
            val doc = postsCollection.document(postId).get().await()
            val authorId = doc.getString("authorId")
            
            if (authorId == userId) {
                postsCollection.document(postId).delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Non autorisé à supprimer ce post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

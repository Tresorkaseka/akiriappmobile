package com.example.akiriapp.data.repository

import com.example.akiriapp.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ReviewRepository {

    private val reviewsCollection = FirebaseFirestore.getInstance().collection("reviews")

    suspend fun getReviewsForCourse(courseId: String): Result<List<Review>> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("courseId", courseId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()
            
            val reviews = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

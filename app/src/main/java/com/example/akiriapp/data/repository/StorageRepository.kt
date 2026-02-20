package com.example.akiriapp.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for handling Firebase Storage operations.
 */
class StorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    /**
     * Upload an image to Firebase Storage and return the download URL.
     */
    suspend fun uploadCourseThumbnail(imageUri: Uri): Result<String> {
        return try {
            val fileName = "course_thumbnails/${UUID.randomUUID()}.jpg"
            val fileRef = storageRef.child(fileName)
            
            fileRef.putFile(imageUri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Upload a lesson video to Firebase Storage and return the download URL.
     */
    suspend fun uploadLessonVideo(videoUri: Uri): Result<String> {
        return try {
            val fileName = "lesson_videos/${UUID.randomUUID()}.mp4"
            val fileRef = storageRef.child(fileName)
            
            fileRef.putFile(videoUri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

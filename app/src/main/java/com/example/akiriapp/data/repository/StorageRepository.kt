package com.example.akiriapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Repository for handling Storage operations.
 * Bypasses Firebase Storage due to Spark plan limitations.
 * Uses Base64 compression for images and advises YouTube links for videos.
 */
class StorageRepository {

    /**
     * Compresses an image and converts it to a Base64 string.
     */
    suspend fun uploadCourseThumbnail(context: Context, imageUri: Uri): Result<String> {
        return try {
            val base64String = compressImageToBase64(context, imageUri)
            Result.success("data:image/jpeg;base64,$base64String")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Converts a ByteArray to a Base64 string. (Useful if already compressed)
     */
    suspend fun uploadCourseThumbnail(imageBytes: ByteArray): Result<String> {
        return try {
            val base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            Result.success("data:image/jpeg;base64,$base64String")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Helper function to compress large images before converting to Base64 to avoid Firestore document limits.
     */
    private fun compressImageToBase64(context: Context, imageUri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (originalBitmap == null) {
            throw IllegalArgumentException("Could not decode image")
        }

        // Resize bitmap to max 800px width/height to save space
        val maxWidth = 800
        val maxHeight = 800
        val scale = minOf(maxWidth.toFloat() / originalBitmap.width, maxHeight.toFloat() / originalBitmap.height)
        
        val resizedBitmap = if (scale < 1) {
            Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * scale).toInt(),
                (originalBitmap.height * scale).toInt(),
                true
            )
        } else {
            originalBitmap
        }

        val outputStream = ByteArrayOutputStream()
        // Compress to JPEG with 70% quality
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Upload a lesson video (Stubbed out due to Storage limits).
     */
    suspend fun uploadLessonVideo(videoUri: Uri): Result<String> {
        return Result.failure(Exception("Les vidéos natives ne sont plus supportées. Veuillez utiliser un lien YouTube ou Vimeo."))
    }
}

package com.example.akiriapp.data.model

/**
 * Represents a forum post in the community section.
 * Stored in Firestore collection: forum_posts/{postId}
 */
data class ForumPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String? = null,
    val title: String = "",
    val content: String = "",
    val topic: String = "", // e.g., "Programmation", "Design", "Marketing"
    val tags: List<String> = emptyList(),
    val likes: Int = 0,
    val commentCount: Int = 0,
    val likedBy: List<String> = emptyList(), // List of user IDs who liked
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", null, "", "", "", emptyList(), 0, 0, emptyList(), 0L)
    
    fun getTimeAgo(): String {
        val diff = System.currentTimeMillis() - createdAt
        val minutes = diff / 60000
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> "il y a ${days}j"
            hours > 0 -> "il y a ${hours}h"
            minutes > 0 -> "il y a ${minutes}min"
            else -> "à l'instant"
        }
    }
}

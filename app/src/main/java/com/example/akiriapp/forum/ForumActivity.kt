package com.example.akiriapp.forum

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.akiriapp.adapter.ForumPostAdapter
import com.example.akiriapp.data.model.ForumPost
import com.example.akiriapp.data.repository.AuthRepository
import com.example.akiriapp.data.repository.ForumRepository
import com.example.akiriapp.databinding.ActivityForumBinding
import kotlinx.coroutines.launch

class ForumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForumBinding
    private val authRepository = AuthRepository()
    private val forumRepository = ForumRepository()
    private lateinit var postAdapter: ForumPostAdapter
    
    private var selectedTopic: String = "Tous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupTopicChips()
        setupFab()
        loadPosts()
    }

    private fun setupAdapter() {
        val userId = authRepository.currentUser?.uid ?: ""
        postAdapter = ForumPostAdapter(
            currentUserId = userId,
            onLikeClick = { post -> handleLike(post) },
            onCommentClick = { post -> handleComment(post) },
            onShareClick = { post -> handleShare(post) }
        )
        binding.rvPosts.adapter = postAdapter
    }

    private fun setupTopicChips() {
        binding.chipGroupTopics.setOnCheckedStateChangeListener { _, checkedIds ->
            // Get selected chip text and reload posts
            val checkedId = checkedIds.firstOrNull() ?: binding.chipAll.id
            selectedTopic = when (checkedId) {
                binding.chipAll.id -> "Tous"
                binding.chipProg.id -> "Programmation"
                binding.chipDesign.id -> "Design"
                binding.chipEnt.id -> "Entrepreneuriat"
                binding.chipNews.id -> "Actualités"
                else -> "Tous"
            }
            loadPosts()
        }
    }

    private fun setupFab() {
        binding.fabNewPost.setOnClickListener {
            showCreatePostDialog()
        }
    }

    private fun loadPosts() {
        lifecycleScope.launch {
            forumRepository.getPosts(selectedTopic).onSuccess { posts ->
                postAdapter.submitList(posts)
            }.onFailure { e ->
                Toast.makeText(this@ForumActivity, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLike(post: ForumPost) {
        val userId = authRepository.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            val isLiked = post.likedBy.contains(userId)
            
            if (isLiked) {
                forumRepository.unlikePost(post.id, userId)
            } else {
                forumRepository.likePost(post.id, userId)
            }
            
            // Reload posts to reflect changes
            loadPosts()
        }
    }

    private fun handleComment(post: ForumPost) {
        // TODO: Navigate to comments screen
        Toast.makeText(this, "Commentaires à venir", Toast.LENGTH_SHORT).show()
    }

    private fun handleShare(post: ForumPost) {
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, "${post.title}\n\n${post.content}")
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(shareIntent, "Partager"))
    }

    private fun showCreatePostDialog() {
        val userId = authRepository.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            authRepository.getCurrentUserProfile().onSuccess { user ->
                // TODO: Show a proper dialog/bottom sheet for creating posts
                // For now, create a sample post
                val newPost = ForumPost(
                    authorId = userId,
                    authorName = user.fullName,
                    title = "Nouveau post de test",
                    content = "Ceci est un post de test créé depuis l'app.",
                    topic = selectedTopic.takeIf { it != "Tous" } ?: "Programmation",
                    tags = listOf("question")
                )
                
                forumRepository.createPost(newPost).onSuccess {
                    Toast.makeText(this@ForumActivity, "Post créé!", Toast.LENGTH_SHORT).show()
                    loadPosts()
                }
            }
        }
    }
}

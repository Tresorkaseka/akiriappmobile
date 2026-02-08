package com.example.akiriapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.akiriapp.data.model.ForumPost
import com.example.akiriapp.databinding.ItemForumPostBinding

/**
 * Adapter for displaying forum posts in a RecyclerView.
 */
class ForumPostAdapter(
    private val currentUserId: String,
    private val onLikeClick: (ForumPost) -> Unit,
    private val onCommentClick: (ForumPost) -> Unit,
    private val onShareClick: (ForumPost) -> Unit
) : ListAdapter<ForumPost, ForumPostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemForumPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(
        private val binding: ItemForumPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: ForumPost) {
            binding.tvAuthorName.text = post.authorName
            binding.tvTimeAgo.text = post.getTimeAgo()
            binding.tvPostTitle.text = post.title
            binding.tvPostContent.text = post.content
            binding.tvLikeCount.text = post.likes.toString()
            binding.tvCommentCount.text = post.commentCount.toString()
            
            // Show tag if available
            if (post.tags.isNotEmpty()) {
                binding.tvTag.text = post.tags.first()
                binding.tvTag.visibility = android.view.View.VISIBLE
            } else {
                binding.tvTag.visibility = android.view.View.GONE
            }
            
            // Highlight if user liked this post
            val isLiked = post.likedBy.contains(currentUserId)
            binding.btnLike.alpha = if (isLiked) 1f else 0.6f
            
            binding.btnLike.setOnClickListener { 
                it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }.start()
                onLikeClick(post) 
            }
            binding.btnComment.setOnClickListener { onCommentClick(post) }
            binding.btnShare.setOnClickListener { onShareClick(post) }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<ForumPost>() {
        override fun areItemsTheSame(oldItem: ForumPost, newItem: ForumPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ForumPost, newItem: ForumPost): Boolean {
            return oldItem == newItem
        }
    }
}

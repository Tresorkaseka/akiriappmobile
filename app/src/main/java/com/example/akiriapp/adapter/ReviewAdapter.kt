package com.example.akiriapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.akiriapp.R
import com.example.akiriapp.data.model.Review

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserInitials: TextView = itemView.findViewById(R.id.tvUserInitials)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val tvComment: TextView = itemView.findViewById(R.id.tvComment)

        fun bind(review: Review) {
            tvUserName.text = review.userName
            // Display first letter of username as initial
            tvUserInitials.text = if (review.userName.isNotEmpty()) review.userName.substring(0, 1).uppercase() else "?"
            
            ratingBar.rating = review.rating
            tvComment.text = review.comment
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
}

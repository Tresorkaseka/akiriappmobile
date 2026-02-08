package com.example.akiriapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.akiriapp.data.model.Enrollment
import com.example.akiriapp.databinding.ItemCourseProgressBinding

/**
 * Adapter for displaying enrollments (course progress) in a RecyclerView.
 */
class EnrollmentAdapter(
    private val onContinueClick: (Enrollment) -> Unit
) : ListAdapter<Enrollment, EnrollmentAdapter.EnrollmentViewHolder>(EnrollmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val binding = ItemCourseProgressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EnrollmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EnrollmentViewHolder(
        private val binding: ItemCourseProgressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(enrollment: Enrollment) {
            binding.tvCourseTitle.text = enrollment.courseTitle
            binding.progressBar.progress = enrollment.progress
            binding.tvProgress.text = "${enrollment.progress}% complété"
            
            binding.btnContinue.setOnClickListener {
                onContinueClick(enrollment)
            }
            
            binding.root.setOnClickListener {
                onContinueClick(enrollment)
            }
        }
    }

    class EnrollmentDiffCallback : DiffUtil.ItemCallback<Enrollment>() {
        override fun areItemsTheSame(oldItem: Enrollment, newItem: Enrollment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Enrollment, newItem: Enrollment): Boolean {
            return oldItem == newItem
        }
    }
}

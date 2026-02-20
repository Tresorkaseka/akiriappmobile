package com.example.akiriapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.akiriapp.data.model.Course
import com.example.akiriapp.databinding.ItemCourseCardBinding

/**
 * Adapter for displaying courses in a RecyclerView.
 */
class CourseAdapter(
    private val onCourseClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(
        private val binding: ItemCourseCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            binding.tvCourseTitle.text = course.title
            binding.tvInstructor.text = course.instructorName
            binding.tvPrice.text = course.getFormattedPrice()
            binding.ratingBar.rating = course.rating
            binding.tvRating.text = String.format("%.1f", course.rating)

            if (!course.thumbnailUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(course.thumbnailUrl)
                    .centerCrop()
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                    .placeholder(com.example.akiriapp.R.drawable.gradient_course_hero)
                    .error(com.example.akiriapp.R.drawable.gradient_course_hero)
                    .into(binding.ivThumbnail)
            } else if (course.imageResId != null) {
                binding.ivThumbnail.setImageResource(course.imageResId)
            } else {
                binding.ivThumbnail.setImageResource(com.example.akiriapp.R.drawable.gradient_course_hero)
            }
            
            binding.root.setOnClickListener {
                onCourseClick(course)
            }
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}

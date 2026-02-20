package com.example.akiriapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.akiriapp.R
import com.example.akiriapp.data.model.Enrollment

class EnrollmentAdapter(
    private val onEnrollmentClick: (Enrollment) -> Unit
) : ListAdapter<Enrollment, EnrollmentAdapter.EnrollmentViewHolder>(EnrollmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enrollment, parent, false)
        return EnrollmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EnrollmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCourseThumbnail: ImageView = itemView.findViewById(R.id.ivCourseThumbnail)
        private val tvCourseTitle: TextView = itemView.findViewById(R.id.tvCourseTitle)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEnrollmentClick(getItem(position))
                }
            }
        }

        fun bind(enrollment: Enrollment) {
            tvCourseTitle.text = enrollment.courseTitle
            
            if (!enrollment.courseThumbnailUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(enrollment.courseThumbnailUrl)
                    .centerCrop()
                    .placeholder(R.drawable.gradient_course_hero)
                    .error(R.drawable.gradient_course_hero)
                    .into(ivCourseThumbnail)
            } else {
                ivCourseThumbnail.setImageResource(R.drawable.gradient_course_hero)
            }
            
            progressBar.progress = enrollment.progress
            tvProgress.text = "${enrollment.progress}%"
            
            if (enrollment.isCompleted()) {
                tvStatus.text = "Terminé"
                tvStatus.setTextColor(itemView.context.getColor(R.color.success)) // Assuming color.success exists
            } else {
                tvStatus.text = "En cours"
                tvStatus.setTextColor(itemView.context.getColor(R.color.primary))
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

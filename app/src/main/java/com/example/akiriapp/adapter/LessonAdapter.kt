package com.example.akiriapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.akiriapp.data.model.Lesson
import com.example.akiriapp.databinding.ItemLessonBinding

class LessonAdapter(
    private val onLessonClicked: (Lesson) -> Unit
) : ListAdapter<Lesson, LessonAdapter.LessonViewHolder>(LessonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemLessonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LessonViewHolder(binding, onLessonClicked)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class LessonViewHolder(
        private val binding: ItemLessonBinding,
        private val onLessonClicked: (Lesson) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson, position: Int) {
            // Using ItemLessonBinding
            binding.tvLessonTitle.text = "Leçon ${position + 1} : ${lesson.title}"
            binding.tvLessonDuration.text = "${lesson.duration} min"
            
            // Assuming there's an icon, we can set a generic play icon
            // binding.ivLessonIcon.setImageResource(android.R.drawable.ic_media_play)

            binding.root.setOnClickListener {
                onLessonClicked(lesson)
            }
        }
    }

    class LessonDiffCallback : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem
        }
    }
}

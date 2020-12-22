package com.example.todolist.tasks

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.databinding.ItemTaskBinding
import com.example.todolist.utils.setOnCheckedChangeListenerSafe
import com.example.todolist.utils.strikeThrough

class TasksAdapter(private val context: Context) :
    ListAdapter<Task, TaskViewHolder>(DIFF_CALLBACK) {

    var onTaskCompleteChanged: ((task: Task, completed: Boolean) -> Unit)? = null

    private var expandedPosition = -1

    private val highlightColor: Int by lazy {
        ContextCompat.getColor(context, R.color.colorHighlight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.binding.apply {
            taskTitleTextView.apply {
                text = task.title
                maxLines = if (position == expandedPosition) Integer.MAX_VALUE else 1
                strikeThrough = task.completed
                setTextColor(if (task.completed) Color.GRAY else Color.BLACK)
            }
            taskCompletedCheckBox.apply {
                isChecked = task.completed
                setOnCheckedChangeListenerSafe { onTaskCompleteChanged?.invoke(task, isChecked) }
            }
            root.apply {
                setBackgroundColor(if (task.completed) highlightColor else Color.TRANSPARENT)
                setOnClickListener {
                    expandedPosition = if (position == expandedPosition) -1 else position
                    post { notifyItemChanged(position) }
                }
            }
        }
    }
}

class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
}
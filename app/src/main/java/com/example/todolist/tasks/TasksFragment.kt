package com.example.todolist.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.databinding.FragmentTasksBinding
import com.example.todolist.utils.setVisible
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class TasksFragment : Fragment() {

    private val viewModel: TasksViewModel by viewModel()

    private lateinit var adapter: TasksAdapter

    private val swipeToDelete = ItemTouchHelper(
        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = adapter.currentList[viewHolder.adapterPosition]
                viewModel.onTaskSwiped(task)
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTasksBinding.inflate(inflater, container, false)
        // handle list
        adapter = TasksAdapter(binding.root.context).apply {
            onTaskCompleteChanged = { task, completed ->
                viewModel.onTaskCompleteChanged(task, completed)
            }
            // scroll to top when task is added
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    binding.tasksRecyclerView.smoothScrollToPosition(0)
                }
            })
        }
        binding.tasksRecyclerView.adapter = adapter
        swipeToDelete.attachToRecyclerView(binding.tasksRecyclerView)
        viewModel.tasks.observe(viewLifecycleOwner) { adapter.submitList(it) }
        // handle actions
        viewModel.actions.observe(viewLifecycleOwner) {
            when (it) {
                is TasksViewModel.Action.ShowLoading ->
                    binding.progressBar.setVisible(it.loading)
                is TasksViewModel.Action.ShowErrorSnackbar ->
                    Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
                is TasksViewModel.Action.ClearInputField ->
                    binding.inputEditText.text.clear()
                is TasksViewModel.Action.ShowTitleBlankError ->
                    binding.inputEditText.error = getString(R.string.taskTitleBlankError)
                is TasksViewModel.Action.ShowUndoSnackbar ->
                    Snackbar.make(binding.root, R.string.taskDeleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undoButton) { viewModel.onUndoTaskDeletion() }
                        .show()
            }
        }
        // handle add task
        binding.addButton.setOnClickListener {
            viewModel.onAddTask(binding.inputEditText.text.toString())
        }
        return binding.root
    }
}
package com.example.todolist.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.todolist.databinding.FragmentTasksBinding
import com.example.todolist.utils.setVisible
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class TasksFragment : Fragment() {

    private val viewModel: TasksViewModel by viewModel()

    private lateinit var adapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTasksBinding.inflate(inflater, container, false)
        // handle list
        adapter = TasksAdapter(binding.root.context)
        binding.tasksRecyclerView.adapter = adapter
        viewModel.tasks.observe(viewLifecycleOwner) { adapter.submitList(it) }
        // handle actions
        viewModel.actions.observe(viewLifecycleOwner) {
            when (it) {
                is TasksViewModel.Action.ShowLoading ->
                    binding.progressBar.setVisible(it.loading)
                is TasksViewModel.Action.ShowErrorSnackbar ->
                    Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
            }
        }
        return binding.root
    }
}
package com.example.todolist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todolist.tasks.Task
import com.example.todolist.tasks.TasksRepository
import com.example.todolist.tasks.TasksViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TasksViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: TasksRepository

    private lateinit var tasksViewModel: TasksViewModel

    @Before
    fun setup() {
        tasksViewModel = TasksViewModel(repository, TestDispatcherProvider)
    }

    @Test
    fun `init() fires loading event and preloads data`() = runBlockingTest {
        verify(repository).preloadData()
        assertEquals(TasksViewModel.Action.ShowLoading(true), tasksViewModel.actions.value)
    }

    @Test
    fun `onAddButtonClick() creates a task and passes it to repository`() = runBlockingTest {
        val taskTitle = "test"
        tasksViewModel.onAddTask(taskTitle)
        verify(repository).addTask(Task(taskTitle))
        assertEquals(TasksViewModel.Action.ClearInputField, tasksViewModel.actions.value)
    }

    @Test
    fun `onAddButtonClick() with blank taskTitle fires error`() = runBlockingTest {
        val taskTitle = "   "
        tasksViewModel.onAddTask(taskTitle)
        assertEquals(TasksViewModel.Action.ShowTitleBlankError, tasksViewModel.actions.value)
    }

    @Test
    fun `onTaskCompleteChanged() passes it to repository`() = runBlockingTest {
        val task = mock<Task>()
        val completed = true
        tasksViewModel.onTaskCompleteChanged(task, completed)
        verify(repository).setTaskCompleted(task, completed)
    }
}
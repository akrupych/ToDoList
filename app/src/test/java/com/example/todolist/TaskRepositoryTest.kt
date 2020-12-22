package com.example.todolist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.todolist.tasks.Task
import com.example.todolist.tasks.TasksDao
import com.example.todolist.tasks.TasksRepository
import com.example.todolist.tasks.TasksService
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
class TaskRepositoryTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var service: TasksService

    @Mock
    lateinit var dao: TasksDao

    private lateinit var tasksRepository: TasksRepository

    private val daoTasksLiveData = MutableLiveData<List<Task>>()


    @Before
    fun setup() {
        whenever(dao.getTasks()).thenReturn(daoTasksLiveData)
        tasksRepository = TasksRepository(service, dao)
    }

    @Test
    fun `tasks update when dao tasks update`() = runBlockingTest {
        val tasks = listOf<Task>(mock())
        daoTasksLiveData.postValue(tasks)
        assertEquals(tasks, tasksRepository.tasks.value)
    }

    @Test
    fun `preloadData() fetches service tasks and saves them to dao`() = runBlockingTest {
        val tasks = listOf<Task>(mock())
        whenever(service.getTasks()).thenReturn(tasks)
        tasksRepository.preloadData()
        verify(service).getTasks()
        verify(dao).insertAll(tasks)
    }

    @Test
    fun `addTask() creates task and saves it to dao`() = runBlockingTest {
        val taskTitle = "test"
        tasksRepository.addTask(Task(taskTitle))
        val taskCaptor = argumentCaptor<Task>()
        verify(dao).insert(taskCaptor.capture())
        assertEquals(taskTitle, taskCaptor.firstValue.title)
    }

    @Test
    fun `setTaskCompleted() updates task in dao`() = runBlockingTest {
        val taskTitle = "test"
        val completed = true
        tasksRepository.setTaskCompleted(Task(taskTitle), completed)
        val taskCaptor = argumentCaptor<Task>()
        verify(dao).update(taskCaptor.capture())
        assertEquals(taskTitle, taskCaptor.firstValue.title)
        assertEquals(completed, taskCaptor.firstValue.completed)
    }
}
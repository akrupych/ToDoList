package com.example.todolist.tasks

import androidx.lifecycle.LiveData

class TasksRepository(
    private val taskService: TasksService,
    private val taskDao: TasksDao
) {
    val tasks: LiveData<List<Task>> = taskDao.getTasks()

    suspend fun preloadData() = taskDao.insertAll(taskService.getTasks())

    suspend fun addTask(task: Task) = taskDao.insert(task)

    suspend fun setTaskCompleted(task: Task, completed: Boolean) =
        taskDao.update(task.copy(completed = completed))

    suspend fun delete(task: Task) = taskDao.delete(task)
}
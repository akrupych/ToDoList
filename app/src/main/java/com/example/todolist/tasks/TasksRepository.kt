package com.example.todolist.tasks

import androidx.lifecycle.LiveData

class TasksRepository(
    private val taskService: TasksService,
    private val taskDao: TasksDao
) {
    val tasks: LiveData<List<Task>> = taskDao.getTasks()

    suspend fun preloadData() = taskDao.insertAll(taskService.getTasks())
}
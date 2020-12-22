package com.example.todolist.tasks

import retrofit2.http.GET

interface TasksService {

    @GET("todos?userId=1")
    suspend fun getTasks(): List<Task>

}
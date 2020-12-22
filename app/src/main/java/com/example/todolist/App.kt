package com.example.todolist

import android.app.Application
import com.example.todolist.tasks.taskModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(taskModule)
        }
    }
}
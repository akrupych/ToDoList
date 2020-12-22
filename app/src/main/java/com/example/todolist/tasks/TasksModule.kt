package com.example.todolist.tasks

import androidx.room.Room
import com.example.todolist.utils.DispatcherProvider
import com.example.todolist.utils.DefaultDispatcherProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val taskModule = module {
    viewModel { TasksViewModel(get(), get()) }
    factory { TasksRepository(get(), get<TasksDatabase>().taskDao()) }
    single {
        Retrofit.Builder()
            .baseUrl("http://jsonplaceholder.typicode.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TasksService::class.java)
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
    single {
        Room.databaseBuilder(
            androidContext(),
            TasksDatabase::class.java,
            TasksDatabase::class.java.simpleName
        ).build()
    }
    single<DispatcherProvider> { DefaultDispatcherProvider }
}
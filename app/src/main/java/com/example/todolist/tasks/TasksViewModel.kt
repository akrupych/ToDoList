package com.example.todolist.tasks

import androidx.lifecycle.*
import com.example.todolist.utils.DispatcherProvider
import com.example.todolist.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class TasksViewModel(
    private val repository: TasksRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val tasks: LiveData<List<Task>> = repository.tasks.map {
        postAction(Action.ShowLoading(false))
        it
    }

    val actions: LiveData<Action> = SingleLiveEvent()

    private var deletedTask: Task? = null

    init {
        viewModelScope.launch(dispatchers.IO) {
            try {
                postAction(Action.ShowLoading(true))
                repository.preloadData()
            } catch (t: Throwable) {
                t.message?.let { postAction(Action.ShowErrorSnackbar(it)) }
            }
        }
    }

    fun onAddTask(taskTitle: String) = viewModelScope.launch(dispatchers.IO) {
        if (taskTitle.isNotBlank()) {
            repository.addTask(Task(taskTitle))
            postAction(Action.ClearInputField)
        } else {
            postAction(Action.ShowTitleBlankError)
        }
    }

    fun onTaskCompleteChanged(task: Task, completed: Boolean) =
        viewModelScope.launch(dispatchers.IO) {
            repository.setTaskCompleted(task, completed)
        }

    fun onTaskSwiped(task: Task) = viewModelScope.launch(dispatchers.IO) {
        repository.delete(task)
        deletedTask = task
        postAction(Action.ShowUndoSnackbar)
    }

    fun onUndoTaskDeletion() = viewModelScope.launch(dispatchers.IO) {
        deletedTask?.let { repository.addTask(it) }
        deletedTask = null
    }

    private fun postAction(action: Action) {
        (actions as MutableLiveData).postValue(action)
    }

    sealed class Action {
        data class ShowLoading(val loading: Boolean) : Action()
        data class ShowErrorSnackbar(val message: String) : Action()
        object ClearInputField : Action()
        object ShowTitleBlankError : Action()
        object ShowUndoSnackbar : Action()
    }
}
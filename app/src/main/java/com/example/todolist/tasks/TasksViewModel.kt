package com.example.todolist.tasks

import androidx.lifecycle.*
import com.example.todolist.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksViewModel(
    private val repository: TasksRepository,
) : ViewModel() {

    val tasks: LiveData<List<Task>> = repository.tasks.map {
        postAction(Action.ShowLoading(false))
        it
    }

    val actions: LiveData<Action> = SingleLiveEvent()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postAction(Action.ShowLoading(true))
                repository.preloadData()
            } catch (t: Throwable) {
                t.message?.let { postAction(Action.ShowErrorSnackbar(it)) }
            }
        }
    }

    private fun postAction(action: Action) {
        (actions as MutableLiveData).postValue(action)
    }

    sealed class Action {
        data class ShowLoading(val loading: Boolean) : Action()
        data class ShowErrorSnackbar(val message: String) : Action()
    }
}
package tronku.project.todotask.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tronku.project.todotask.database.RoomDB
import tronku.project.todotask.database.TaskModel

class AddEditViewModel : ViewModel() {

    private var mutableIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = mutableIsLoading

    fun addTask(db: RoomDB, task: TaskModel) {
        mutableIsLoading.postValue(true)
        viewModelScope.launch {
            db.taskDao().insertTask(task)
            mutableIsLoading.postValue(false)
        }
    }

    fun updateTask(db: RoomDB, task: TaskModel) {
        mutableIsLoading.postValue(true)
        viewModelScope.launch {
            db.taskDao().updateTask(task)
            mutableIsLoading.postValue(false)
        }
    }

}
package tronku.project.todotask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tronku.project.todotask.database.RoomDB
import tronku.project.todotask.database.TaskModel

class MainViewModel : ViewModel() {

    fun deleteTask(db: RoomDB, task: TaskModel) {
        viewModelScope.launch {
            db.taskDao().deleteTask(task)
        }
    }

    fun updateTask(db: RoomDB, task: TaskModel) {
        viewModelScope.launch {
            db.taskDao().updateTask(task)
        }
    }

}
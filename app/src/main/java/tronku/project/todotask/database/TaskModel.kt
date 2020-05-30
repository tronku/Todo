package tronku.project.todotask.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class TaskModel (
    @PrimaryKey var id: Int,
    var title: String?,
    var hours: Short,
    var minutes: Short,
    var category: Short,
    var priority: Short,
    var isDone: Boolean = false
)
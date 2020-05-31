package tronku.project.todotask.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskModel::class], version = 2)
abstract class RoomDB : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var instance: RoomDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, RoomDB::class.java, "tasks.db")
                .fallbackToDestructiveMigration().build()
    }

}
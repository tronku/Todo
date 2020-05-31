package tronku.project.todotask.activities

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_add_task.*
import tronku.project.todotask.R
import tronku.project.todotask.database.RoomDB
import tronku.project.todotask.database.TaskModel
import tronku.project.todotask.viewmodels.AddEditViewModel
import java.util.*

class AddEditTaskActivity : AppCompatActivity() {

    private val db: RoomDB by lazy { RoomDB(this) }
    private val viewModel: AddEditViewModel by lazy { AddEditViewModel() }

    private var taskHours: Short = 10
    private var taskMins: Short = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        initialize()
        setClickListeners()
    }

    private fun initialize() {
        if (intent.getIntExtra("action", MainActivity.Action.ADD.ordinal) == MainActivity.Action.ADD.ordinal) {
            addTaskBtn.visibility = View.VISIBLE
            updateTaskBtn.visibility = View.GONE
            addTaskBtn.setOnClickListener {
                addTaskToDB(false)
            }
        } else {
            addTaskBtn.visibility = View.GONE
            updateTaskBtn.visibility = View.VISIBLE
            updateTaskBtn.setOnClickListener {
                addTaskToDB(true)
            }
        }
    }

    private fun setClickListeners() {
        taskTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val hours = cal.get(Calendar.HOUR_OF_DAY)
            val minutes = cal.get(Calendar.MINUTE)
            val timerDialog = TimePickerDialog(this@AddEditTaskActivity,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    taskHours = hourOfDay.toShort()
                    taskMins = minute.toShort()
                    taskTime.text = getTime(hourOfDay, minute)
                }, hours, minutes, false)
            timerDialog.show()
        }

        viewModel.isLoading.observe(this, androidx.lifecycle.Observer {
            addTaskBtn.isEnabled = !it
            updateTaskBtn.isEnabled = !it
            if (!it)
                onBackPressed()
        })
    }

    private fun getTime(hours: Int, min: Int): String {
        return if (hours < 12)
            "$hours:$min AM"
        else
            "${hours-12}:$min PM"
    }

    private fun addTaskToDB(isUpdate: Boolean) {
        val title = taskTitle.text.toString()
        val categoryId = getCategory()
        val priorityId = getPriority()

        val task = TaskModel(
            title = title,
            hours = taskHours,
            minutes = taskMins,
            category = categoryId,
            priority = priorityId
        )

        if (isUpdate)
            viewModel.updateTask(db, task)
        else
            viewModel.addTask(db, task)
    }

    private fun getCategory(): Short {
        return when (taskCategory.checkedRadioButtonId) {
            R.id.personalCategory -> 1.toShort()
            else -> 2.toShort()
        }
    }

    private fun getPriority(): Short {
        return when(taskPriority.checkedRadioButtonId) {
            R.id.highPriority -> 1.toShort()
            R.id.mediumPriority -> 2.toShort()
            else -> 3.toShort()
        }
    }
}
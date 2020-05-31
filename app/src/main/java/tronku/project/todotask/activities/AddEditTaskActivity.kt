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
                addTaskToDB()
            }
        } else {
            fillData()
            addTaskBtn.visibility = View.GONE
            updateTaskBtn.visibility = View.VISIBLE
            updateTaskBtn.setOnClickListener {
                updateTaskToDB()
            }
        }
    }

    private fun fillData() {
        val bundle = intent.getBundleExtra("task")
        taskTitle.setText(bundle.getString("title", "Title"))

        taskHours = bundle.getInt("hours", 10).toShort()
        taskMins = bundle.getInt("mins", 0).toShort()
        taskTime.text = getTime(taskHours.toInt(), taskMins.toInt())

        taskCategory.check(getCategoryId(bundle.getInt("category", 0)))
        taskPriority.check(getPriorityId(bundle.getInt("priority", 0)))

        taskTitle.setSelection(taskTitle.text.length)
    }

    private fun getCategoryId(id: Int): Int {
        return if (id == 0)
            R.id.personalCategory
        else
            R.id.workCategory
    }

    private fun getPriorityId(id: Int): Int {
        return when (id) {
            0 -> R.id.highPriority
            1 -> R.id.mediumPriority
            else -> R.id.lowPriority
        }
    }

    private fun setClickListeners() {
        taskTime.setOnClickListener {
            val hours = taskHours.toInt()
            val minutes = taskMins.toInt()

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
        return when {
            hours < 12 -> "$hours:$min AM"
            hours > 12 -> "${hours-12}:$min PM"
            hours == 0 -> "${hours+12}:$min AM"
            else -> "$hours:$min PM"
        }
    }

    private fun addTaskToDB() {
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

        viewModel.addTask(db, task)
    }

    private fun updateTaskToDB() {
        val title = taskTitle.text.toString()
        val categoryId = getCategory()
        val priorityId = getPriority()
        val id = intent.getBundleExtra("task").getInt("id")

        val task = TaskModel(
            id = id,
            title = title,
            hours = taskHours,
            minutes = taskMins,
            category = categoryId,
            priority = priorityId
        )

        viewModel.updateTask(db, task)
    }

    private fun getCategory(): Short {
        return when (taskCategory.checkedRadioButtonId) {
            R.id.personalCategory -> 0.toShort()
            else -> 1.toShort()
        }
    }

    private fun getPriority(): Short {
        return when(taskPriority.checkedRadioButtonId) {
            R.id.highPriority -> 0.toShort()
            R.id.mediumPriority -> 1.toShort()
            else -> 2.toShort()
        }
    }
}
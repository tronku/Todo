package tronku.project.todotask.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import tronku.project.todotask.R
import tronku.project.todotask.adapters.TaskClickAction
import tronku.project.todotask.adapters.TasksAdapter
import tronku.project.todotask.database.RoomDB
import tronku.project.todotask.database.TaskModel
import tronku.project.todotask.viewmodels.MainViewModel

class MainActivity : AppCompatActivity(), TasksAdapter.OnTaskClickListener {

    private val db: RoomDB by lazy { RoomDB(this) }
    private val viewModel: MainViewModel by lazy { MainViewModel() }
    private val adapter: TasksAdapter by lazy { TasksAdapter(this, this) }
    private var taskList = listOf<TaskModel>()

    enum class Action {
        ADD,
        UPDATE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
        setClickListeners()
    }

    private fun initialize() {
        tasksRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tasksRecyclerView.adapter = adapter

        db.taskDao().getAllTasks().observe(this, Observer {
            if (it.isNullOrEmpty()) {
                emptyLayout.visibility = View.VISIBLE
                loadingLayout.visibility = View.GONE
                tasksRecyclerView.visibility = View.GONE
                searchEditText.visibility = View.GONE
                addFloatingBtn.hide()
            } else {
                emptyLayout.visibility = View.GONE
                loadingLayout.visibility = View.GONE
                tasksRecyclerView.visibility = View.VISIBLE
                searchEditText.visibility = View.VISIBLE
                addFloatingBtn.show()
                taskList = it
                adapter.submitList(taskList)
            }
        })
    }

    private fun setClickListeners() {
        val addIntent = Intent(this, AddEditTaskActivity::class.java)
        addIntent.putExtra("action", Action.ADD.ordinal)

        emptyAddBtn.setOnClickListener {
            startActivity(addIntent)
        }

        addFloatingBtn.setOnClickListener {
            startActivity(addIntent)
        }

        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

        })
    }

    private fun filter(query: CharSequence) {
        val filteredList = ArrayList<TaskModel>()
        taskList.forEach {
            if (it.title.toString().toLowerCase().contains(query)) {
                filteredList.add(it)
            }
        }
        adapter.submitList(filteredList)
    }

    override fun onTaskClicked(task: TaskModel, action: TaskClickAction) {
        when (action) {
            TaskClickAction.DELETE -> viewModel.deleteTask(db, task)
            TaskClickAction.UPDATE -> viewModel.updateTask(db, task)
            else -> {
                val navIntent = Intent(this@MainActivity, AddEditTaskActivity::class.java)
                val bundle = bundleOf(
                    "id" to task.id,
                    "title" to task.title,
                    "hours" to task.hours.toInt(),
                    "mins" to task.minutes.toInt(),
                    "category" to task.category.toInt(),
                    "priority" to task.priority.toInt()
                )
                navIntent.putExtra("task", bundle)
                navIntent.putExtra("action", Action.UPDATE.ordinal)
                startActivity(navIntent)
            }
        }
    }

}
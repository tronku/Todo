package tronku.project.todotask.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import tronku.project.todotask.R
import tronku.project.todotask.adapters.TasksAdapter
import tronku.project.todotask.database.RoomDB
import tronku.project.todotask.viewmodels.MainViewModel
import javax.xml.transform.Source

class MainActivity : AppCompatActivity() {

    private val db: RoomDB by lazy { RoomDB(this) }
    private val viewModel: MainViewModel by lazy { MainViewModel() }
    private val adapter: TasksAdapter by lazy { TasksAdapter(this) }

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
                adapter.submitList(it)
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
    }
}
package tronku.project.todotask.adapters

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tronku.project.todotask.R
import tronku.project.todotask.database.TaskModel
import kotlinx.android.synthetic.main.task_item_layout.view.*

enum class TaskClickAction {
    DELETE,
    UPDATE,
    NAVIGATE
}

class TasksAdapter(private val context: Context, private val onTaskClickListener: OnTaskClickListener) :
    ListAdapter<TaskModel, TasksAdapter.ViewHolder>(TaskDiffCallback) {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(task: TaskModel) {
            itemView.taskTitle.text = task.title
            itemView.taskCategory.text = getCategory(task.category)
            itemView.taskTime.text = getTime(task.hours, task.minutes)
            setPriority(task.priority)
            setDoneBtn(task.isDone)
            setClickListeners(task)
        }

        private fun setDoneBtn(isDone: Boolean) {
            if (isDone) {
                itemView.taskDoneBtn.setImageResource(R.drawable.ic_tick_active)
                itemView.taskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                itemView.taskDoneBtn.setImageResource(R.drawable.ic_tick_inactive)
                itemView.taskTitle.paintFlags = 0
            }
        }

        private fun setClickListeners(task: TaskModel) {
            itemView.taskDoneBtn.setOnClickListener {
                task.isDone = !task.isDone
                setDoneBtn(task.isDone)
                onTaskClickListener.onTaskClicked(task, TaskClickAction.UPDATE)
            }

            itemView.taskDelete.setOnClickListener {
                onTaskClickListener.onTaskClicked(task, TaskClickAction.DELETE)
            }

            itemView.taskLayout.setOnClickListener {
                if (!task.isDone) {
                    onTaskClickListener.onTaskClicked(task, TaskClickAction.NAVIGATE)
                }
            }
        }

        private fun getCategory(categoryId: Short): String {
            return if (categoryId.toInt() == 0)
                "PERSONAL"
            else
                "WORK"
        }

        private fun getTime(hours: Short, min: Short): String {
            return when {
                hours < 12 -> "$hours:$min AM"
                hours > 12 -> "${hours-12}:$min PM"
                hours.toInt() == 0 -> "${hours+12}:$min AM"
                else -> "$hours:$min PM"
            }
        }

        private fun setPriority(priority: Short) {
            when (priority.toInt()) {
                0 -> {
                    itemView.taskPriority.text = "HIGH"
                    itemView.taskPriority.background.colorFilter =
                        PorterDuffColorFilter(context.resources.getColor(R.color.priorityHigh), PorterDuff.Mode.SRC_IN)
                }
                1 -> {
                    itemView.taskPriority.text = "MEDIUM"
                    itemView.taskPriority.background.colorFilter =
                        PorterDuffColorFilter(context.resources.getColor(R.color.priorityMedium), PorterDuff.Mode.SRC_IN)
                }
                2 -> {
                    itemView.taskPriority.text = "LOW"
                    itemView.taskPriority.background.colorFilter =
                        PorterDuffColorFilter(context.resources.getColor(R.color.priorityLow), PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    object TaskDiffCallback: DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem == newItem
        }

    }

    interface OnTaskClickListener {
        fun onTaskClicked(task: TaskModel, action: TaskClickAction)
    }

}
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

class TasksAdapter(private val context: Context) : ListAdapter<TaskModel, TasksAdapter.ViewHolder>(TaskDiffCallback) {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(task: TaskModel) {
            itemView.taskTitle.text = task.title
            itemView.taskCategory.text = getCategory(task.category)
            itemView.taskTime.text = getTime(task.hours, task.minutes)
            setPriority(task.priority)

            if (task.isDone) {
                itemView.taskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        private fun getCategory(categoryId: Short): String {
            return if (categoryId.toInt() == 1)
                "PERSONAL"
            else
                "WORK"
        }

        private fun getTime(hours: Short, min: Short): String {
            return if (hours < 12)
                "$hours:$min AM"
            else
                "${hours-12}:$min PM"
        }

        private fun setPriority(priority: Short) {
            when (priority.toInt()) {
                1 -> {
                    itemView.taskPriority.text = "HIGH"
                    itemView.taskPriority.background.colorFilter =
                        PorterDuffColorFilter(context.resources.getColor(R.color.priorityHigh), PorterDuff.Mode.SRC_IN)
                }
                2 -> {
                    itemView.taskPriority.text = "MEDIUM"
                    itemView.taskPriority.background.colorFilter =
                        PorterDuffColorFilter(context.resources.getColor(R.color.priorityMedium), PorterDuff.Mode.SRC_IN)
                }
                3 -> {
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

}
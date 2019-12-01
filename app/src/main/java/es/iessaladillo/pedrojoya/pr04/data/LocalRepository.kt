package es.iessaladillo.pedrojoya.pr04.data

import android.annotation.SuppressLint
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object LocalRepository : Repository {
    private var tasks: MutableList<Task> = mutableListOf()

    @SuppressLint("SimpleDateFormat")
    private val sdf: DateFormat = SimpleDateFormat("HH:mm:ss")
    private var date = Date()

    override fun queryAllTasks(): List<Task> {
        return tasks

    }

    override fun queryCompletedTasks(): List<Task> {
        val tasksCompleted: MutableList<Task> = mutableListOf()
        tasks.forEach {
            if (it.completed) {
                tasksCompleted.add(it)
            }
        }
        return tasksCompleted.toList()
    }


    override fun queryPendingTasks(): List<Task> {
        val tasksPending: MutableList<Task> = mutableListOf()
        tasks.forEach {
            if (!it.completed) {
                tasksPending.add(it)
            }
        }
        return tasksPending.toList()
    }

    override fun addTask(concept: String) {
        date = Date()
        val id: Long = if (tasks.isEmpty()) {
            1
        } else {
            (tasks[tasks.size - 1].id) + 1
        }
        val task = Task(id, concept, "Created at: ${sdf.format(date)}", false, "No completed")
        tasks.add(task)
    }

    override fun insertTask(task: Task) {
        tasks.add(task)
    }

    override fun deleteTask(taskId: Long) {
        var position: Int=-1
        tasks.forEach {
            if (it.id == taskId) {
                position=tasks.indexOf(it)
            }
        }
        if(position>-1) {
            tasks.removeAt(position)
        }
    }

    override fun deleteTasks(taskIdList: List<Long>) {
        if(taskIdList.isNotEmpty()) {
            taskIdList.forEach {
                deleteTask(it)
            }
        }
    }

    override fun markTaskAsCompleted(taskId: Long) {
        date = Date()
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = true
                it.completedAt = "Completed at: ${sdf.format(date)}"
            }
        }
    }

    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        tasks.forEach {
            if (taskIdList.contains(it.id)) {
                markTaskAsCompleted(it.id)
            }
        }
    }

    override fun markTaskAsPending(taskId: Long) {
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = false
            }
        }
    }

    override fun markTasksAsPending(taskIdList: List<Long>) {
        tasks.forEach {
            if (taskIdList.contains(it.id)) {
                markTaskAsPending(it.id)
            }
        }
    }

}
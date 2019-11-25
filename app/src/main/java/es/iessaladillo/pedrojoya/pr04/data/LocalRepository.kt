package es.iessaladillo.pedrojoya.pr04.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Crea una clase llamada LocalRepository que implemente la interfaz Repository
//  usando una lista mutable para almacenar las tareas.
//  Los id de las tareas se irán generando secuencialmente a partir del valor 1 conforme
//  se van agregando tareas (add).

object LocalRepository : Repository {
    private var tasks: MutableList<Task> = mutableListOf(
        Task(1,"Cocinar","Ahora",false,"Aun nada")
    )


    private val tasksLiveData: MutableLiveData<List<Task>> = MutableLiveData(tasks)


    override fun queryAllTasks(): List<Task> = tasksLiveData.value!!

    override fun queryCompletedTasks(): List<Task> {
        var tasksCompleted: MutableList<Task> = mutableListOf()
        tasks.forEach {
            if (it.completed) {
                tasksCompleted.add(it)
            }
        }
        return tasksCompleted.toList()
    }


    override fun queryPendingTasks(): List<Task> {
        var tasksPending: MutableList<Task> = mutableListOf()
        tasks.forEach {
            if (!it.completed) {
                tasksPending.add(it)
            }
        }
        return tasksPending.toList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addTask(concept: String) {
        val dateTime = LocalDateTime.now()
        val formatTime: String = dateTime.format(
            DateTimeFormatter.ofPattern("M/d/y , H:m:ss")
        )
        val id: Long
        if (tasks.isEmpty()) {
            id = 1
        } else {
            id = (tasks[tasks.size - 1].id) + 1
        }
        val task = Task(id, concept, formatTime, false, "No completed")
        tasks.add(task)
        tasksLiveData.value = ArrayList<Task>(tasks)
    }

    override fun insertTask(task: Task) {
        tasks.add(task)
    }

    override fun deleteTask(taskId: Long) {
        tasks.forEach {
            if (it.id == taskId) {
                tasks.remove(it)
            }
        }
    }

    override fun deleteTasks(taskIdList: List<Long>) {
        tasks.forEach {
            if (taskIdList.contains(it.id)) {
                tasks.remove(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTaskAsCompleted(taskId: Long) {
        val dateTime = LocalDateTime.now()
        val formatTime: String = dateTime.format(
            DateTimeFormatter.ofPattern("M/d/y , H:m:ss")
        )
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = true
                it.completedAt = formatTime
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        val dateTime = LocalDateTime.now()
        val formatTime: String = dateTime.format(
            DateTimeFormatter.ofPattern("M/d/y , H:m:ss")
        )
        tasks.forEach {
            it.completed = true
            it.completedAt = formatTime

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
            it.completed = false

        }
    }

}


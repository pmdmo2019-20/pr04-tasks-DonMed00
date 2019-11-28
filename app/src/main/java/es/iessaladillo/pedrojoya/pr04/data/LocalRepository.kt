package es.iessaladillo.pedrojoya.pr04.data

import android.os.Build
import androidx.annotation.RequiresApi
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// TODO: Crea una clase llamada LocalRepository que implemente la interfaz Repository
//  usando una lista mutable para almacenar las tareas.
//  Los id de las tareas se irán generando secuencialmente a partir del valor 1 conforme
//  se van agregando tareas (add).

object LocalRepository : Repository {
    private var tasks: MutableList<Task> = mutableListOf()


    //private val tasksLiveData: MutableLiveData<List<Task>> = MutableLiveData(tasks)


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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addTask(concept: String) {
        val dateTime = LocalDateTime.now()
        val formatTime: String = dateTime.format(
            DateTimeFormatter.ofPattern("M/d/y , HH:mm:ss")
        )
        val id: Long = if (tasks.isEmpty()) {
            1
        } else {
            (tasks[tasks.size - 1].id) + 1
        }
        val task = Task(id, concept, "Created at: $formatTime", false, "No completed")
        tasks.add(task)
        //tasksLiveData.value = ArrayList<Task>(tasks)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTaskAsCompleted(taskId: Long) {
        val dateTime = LocalDateTime.now()
        val formatTime: String = dateTime.format(
            DateTimeFormatter.ofPattern("M/d/y , H:m:ss")
        )
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = true
                it.completedAt = "Completed at: $formatTime"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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


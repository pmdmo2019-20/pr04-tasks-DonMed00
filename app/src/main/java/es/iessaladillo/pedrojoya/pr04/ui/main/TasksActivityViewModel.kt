package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.Event
import es.iessaladillo.pedrojoya.pr04.data.Repository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.isActivityAvailable
import es.iessaladillo.pedrojoya.pr04.utils.sendTasks

class TasksActivityViewModel(
    private val repository: Repository,
    private val application: Application
) : ViewModel() {

    // Estado de la interfaz

    private val _tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>>
        get() = _tasks


    private val taskIdList: MutableList<Long> = mutableListOf()

    init {
        refreshLists(repository.queryAllTasks())

    }

    private fun refreshLists(newList: List<Task>) {
        _tasks.value = newList.sortedByDescending { it.id }
        taskIdList.clear()
        tasks.value?.forEach {
            taskIdList.add(it.id)
        }

    }

    private val _currentFilter: MutableLiveData<TasksActivityFilter> =
        MutableLiveData(TasksActivityFilter.ALL)

    private val _currentFilterMenuItemId: MutableLiveData<Int> =
        MutableLiveData(R.id.mnuFilterAll)
    val currentFilterMenuItemId: LiveData<Int>
        get() = _currentFilterMenuItemId

    private val _activityTitle: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_title_all))
    val activityTitle: LiveData<String>
        get() = _activityTitle

    private val _lblEmptyViewText: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_no_tasks_yet))
    val lblEmptyViewText: LiveData<String>
        get() = _lblEmptyViewText

    // Eventos de comunicación con la actividad

    private val _onStartActivity: MutableLiveData<Event<Intent>> = MutableLiveData()
    val onStartActivity: LiveData<Event<Intent>>
        get() = _onStartActivity

    private val _onShowMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val onShowMessage: LiveData<Event<String>>
        get() = _onShowMessage

    private val _onShowTaskDeleted: MutableLiveData<Event<Task>> = MutableLiveData()
    val onShowTaskDeleted: LiveData<Event<Task>>
        get() = _onShowTaskDeleted


    // Hace que se muestre en el RecyclerView todas las tareas.
    fun filterAll() {
        _currentFilterMenuItemId.value = R.id.mnuFilterAll
        _activityTitle.value = application.getString(R.string.tasks_title_all)
        queryTasks(TasksActivityFilter.ALL)

    }

    // Hace que se muestre en el RecyclerView sólo las tareas completadas.
    fun filterCompleted() {
        _currentFilterMenuItemId.value = R.id.mnuFilterCompleted
        _activityTitle.value = application.getString(R.string.tasks_title_completed)
        queryTasks(TasksActivityFilter.COMPLETED)


    }

    // Hace que se muestre en el RecyclerView sólo las tareas pendientes.
    fun filterPending() {
        _currentFilterMenuItemId.value = R.id.mnuFilterPending
        _activityTitle.value = application.getString(R.string.tasks_title_pending)
        queryTasks(TasksActivityFilter.PENDING)


    }

    // Hecho
    fun addTask(concept: String) {
        repository.addTask(concept)
        queryTasks(TasksActivityFilter.ALL)
    }

    // Agrega la tarea
    fun insertTask(task: Task) {
        repository.insertTask(task)
        queryTasks(_currentFilter.value!!)
    }

    // Borra la tarea
    fun deleteTask(task: Task) {
        repository.deleteTask(task.id)
        queryTasks(_currentFilter.value!!)


    }

    // Hecho
    fun deleteTasks() {
        if (_tasks.value?.isNotEmpty() == true) {
            repository.deleteTasks(taskIdList)
        } else {
            _onShowMessage.value = Event(application.getString(R.string.tasks_no_tasks_to_delete))
        }
        queryTasks(_currentFilter.value!!)


    }

    // Hecho
    fun markTasksAsCompleted() {
        if (_tasks.value?.isNotEmpty() == true) {
            repository.markTasksAsCompleted(taskIdList)
        } else {
            _onShowMessage.value =
                Event(application.getString(R.string.tasks_no_tasks_to_mark_as_completed))
        }
        queryTasks(_currentFilter.value!!)

    }

    // Hecho
    fun markTasksAsPending() {
        if (_tasks.value?.isNotEmpty() == true) {
            repository.markTasksAsPending(taskIdList)
        } else {
            _onShowMessage.value =
                Event(application.getString(R.string.tasks_no_tasks_to_mark_as_pending))
        }
        queryTasks(_currentFilter.value!!)

    }

    // Hecho
    fun shareTasks() {
        if (_tasks.value?.isNotEmpty() == true) {
            val intent = sendTasks(_tasks)
            _onStartActivity.value = Event(intent)

            if (isActivityAvailable(application, intent)) {
                application.startActivity(intent)
            }
        } else {
            _onShowMessage.value = Event(application.getString(R.string.tasks_no_tasks_to_share))
        }
    }

    //Hecho
    fun updateTaskCompletedState(task: Task, isCompleted: Boolean) {
        if (!isCompleted) {
            repository.markTaskAsCompleted(task.id)
        } else {
            repository.markTaskAsPending(task.id)
        }
        queryTasks(_currentFilter.value!!)
    }


    //Hecho
    fun isValidConcept(concept: String): Boolean {
        return !concept.isBlank()
    }

    // Pide las tareas al repositorio, atendiendo al filtro recibido
    private fun queryTasks(filter: TasksActivityFilter) {
        when (filter) {
            TasksActivityFilter.ALL ->
                refreshLists(repository.queryAllTasks())
            TasksActivityFilter.COMPLETED ->
                refreshLists(repository.queryCompletedTasks())
            TasksActivityFilter.PENDING ->
                refreshLists(repository.queryPendingTasks())
        }
        _currentFilter.value = filter
    }


}


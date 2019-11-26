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

class TasksActivityViewModel(
    private val repository: Repository,
    private val application: Application
) : ViewModel() {

    // Estado de la interfaz

    private val _tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>>
        get() = _tasks


    private var filterNow : TasksActivityFilter? =null
    val taskIdList: MutableList<Long> = mutableListOf()

    init {
        refreshLists(repository.queryAllTasks())

    }

    private fun refreshLists(newList: List<Task>) {
        _tasks.value = newList
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
        queryTasks(TasksActivityFilter.ALL)

    }

    // Hace que se muestre en el RecyclerView sólo las tareas completadas.
    fun filterCompleted() {
        queryTasks(TasksActivityFilter.COMPLETED)


    }

    // Hace que se muestre en el RecyclerView sólo las tareas pendientes.
    fun filterPending() {
        queryTasks(TasksActivityFilter.PENDING)


    }

    // Agrega una nueva tarea con dicho concepto. Si la se estaba mostrando
    // la lista de solo las tareas completadas, una vez agregada se debe
    // mostrar en el RecyclerView la lista con todas las tareas, no sólo
    // las completadas.
    fun addTask(concept: String) {
        repository.addTask(concept)
        queryTasks(_currentFilter.value!!)
    }

    // Agrega la tarea
    fun insertTask(task: Task) {
        repository.insertTask(task)
    }

    // Borra la tarea
    fun deleteTask(task: Task) {
        repository.deleteTask(task.id)
    }

    // Borra todas las tareas mostradas actualmente en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que borrar.
    fun deleteTasks() {
        repository.deleteTasks(taskIdList)
        queryTasks(_currentFilter.value!!)
    }

    // Marca como completadas todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban completadas.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como completadas.
    fun markTasksAsCompleted() {
        //refreshLists(repository.queryAllTasks())
        repository.markTasksAsCompleted(taskIdList)
        queryTasks(_currentFilter.value!!)

    }

    // Marca como pendientes todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban pendientes.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como pendientes.
    fun markTasksAsPending() {
        repository.markTasksAsPending(taskIdList)
        queryTasks(_currentFilter.value!!)

    }

    // Hace que se envíe un Intent con la lista de tareas mostradas actualmente
    // en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un Snackbar indicando
    // que no hay tareas que compartir.
    fun shareTasks() {
        //TODO
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
        _currentFilter.value=filter
    }


}


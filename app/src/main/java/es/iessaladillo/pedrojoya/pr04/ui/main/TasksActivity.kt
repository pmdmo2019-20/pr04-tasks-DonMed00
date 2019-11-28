package es.iessaladillo.pedrojoya.pr04.ui.main

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo

import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.observeEvent
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import es.iessaladillo.pedrojoya.pr04.utils.setOnSwipeListener
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator

import kotlinx.android.synthetic.main.tasks_activity.*


class TasksActivity : AppCompatActivity() {

    private var mnuFilter: MenuItem? = null
    private val viewModel: TasksActivityViewModel by viewModels {
        TasksActivityViewModelFactory(LocalRepository, application)
    }
    private val listAdapter: TasksActivityAdapter = TasksActivityAdapter().also {
        it.onItemClickListener = { position ->
            changeTask(position)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        mnuFilter = menu.findItem(R.id.mnuFilter)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupViews()
        observeViewModelData()
    }

    private fun observeViewModelData() {
        observeTasks()
        observeMenu()
        observeEvent()
    }

    private fun observeEvent() {
        viewModel.onShowMessage.observeEvent(this) {
            Snackbar.make(lstTasks, it, Snackbar.LENGTH_SHORT).show()
        }

    }


    private fun observeMenu() {
        viewModel.activityTitle.observe(this) {
            this.title = it
        }
        viewModel.currentFilterMenuItemId.observe(this) {
            checkMenuItem(it)
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViews() {
        setupRecyclerView()
        txtConcept.setOnEditorActionListener { _, actionId, _ ->
            onEditorAction(actionId)
        }
        imgAddTask.setOnClickListener { addTask() }
    }

    private fun onEditorAction(actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_DONE) {
            addTask()
            true
        } else {
            false
        }
    }

    private fun addTask() {
        if (viewModel.isValidConcept(txtConcept.text.toString())) {
            imgAddTask.hideKeyboard()
            viewModel.addTask(txtConcept.text.toString())
        }
        txtConcept.setText("")


    }

    private fun deleteTask(adapterPosition: Int) {
        val task = listAdapter.getItem(adapterPosition)
        viewModel.deleteTask(task)
        Snackbar.make(
            lstTasks,
            getString(R.string.main_task_deleted, task.concept),
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.main_undo) { viewModel.insertTask(task) }
            .show()
    }


    private fun setupRecyclerView() {
        lstTasks.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            itemAnimator = FlipInTopXAnimator()
            adapter = listAdapter

            setOnSwipeListener { viewHolder, _ ->
                deleteTask(viewHolder.adapterPosition)
            }
        }
    }

    private fun observeTasks() {
        viewModel.tasks.observe(this) {
            showTasks(it)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuShare -> viewModel.shareTasks()
            R.id.mnuDelete -> viewModel.deleteTasks()
            R.id.mnuComplete -> viewModel.markTasksAsCompleted()
            R.id.mnuPending -> viewModel.markTasksAsPending()
            R.id.mnuFilterAll -> viewModel.filterAll()
            R.id.mnuFilterPending -> viewModel.filterPending()
            R.id.mnuFilterCompleted -> viewModel.filterCompleted()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun checkMenuItem(@MenuRes menuItemId: Int) {
        lstTasks.post {
            val item = mnuFilter?.subMenu?.findItem(menuItemId)
            item?.let { menuItem ->
                menuItem.isChecked = true
            }
        }
    }

    private fun showTasks(tasks: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(tasks)
            lblEmptyView.invisibleUnless(tasks.isEmpty())
        }
    }

    private fun changeTask(position: Int) {
        val task = listAdapter.getItem(position)
        viewModel.updateTaskCompletedState(task, task.completed)


    }

}


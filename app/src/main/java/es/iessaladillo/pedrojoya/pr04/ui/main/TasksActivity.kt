package es.iessaladillo.pedrojoya.pr04.ui.main

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.observeEvent
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.Repository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import es.iessaladillo.pedrojoya.pr04.utils.setOnSwipeListener
import kotlinx.android.synthetic.main.tasks_activity.*
import kotlinx.android.synthetic.main.tasks_activity_item.*


class TasksActivity : AppCompatActivity() {

    private var mnuFilter: MenuItem? = null
    private val viewModel: TasksActivityViewModel by viewModels {
        TasksActivityViewModelFactory(LocalRepository, application)
    }
    private val listAdapter: TasksActivityAdapter = TasksActivityAdapter()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        mnuFilter = menu.findItem(R.id.mnuFilter)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupViews()
        observeTasks() }




    private fun setupViews() {
        setupRecyclerView()
        imgAddTask.setOnClickListener { addTask() }

    }

    private fun addTask() {
        viewModel.addTask(txtConcept.text.toString())
        lblConcept.hideKeyboard()
    }

    private fun setupRecyclerView() {
        lstTasks.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            itemAnimator = DefaultItemAnimator()
            adapter = listAdapter
        }
    }

    private fun observeTasks() {
        viewModel.tasks.observe(this){
            updateList(it)
        }
    }
    private fun updateList(newList : List<Task>) {
        listAdapter.submitList(newList)
        if(newList.isEmpty()){
            lblEmptyView.invisibleUnless(true)
        }else{
            lblEmptyView.invisibleUnless(false)
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

}


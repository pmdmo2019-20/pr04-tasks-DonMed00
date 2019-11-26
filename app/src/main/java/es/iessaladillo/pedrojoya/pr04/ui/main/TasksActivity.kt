package es.iessaladillo.pedrojoya.pr04.ui.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast

import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import kotlinx.android.synthetic.main.tasks_activity.*
import kotlinx.android.synthetic.main.tasks_activity_item.*


class TasksActivity : AppCompatActivity() {

    private var mnuFilter: MenuItem? = null
    private val viewModel: TasksActivityViewModel by viewModels {
        TasksActivityViewModelFactory(LocalRepository, application)
    }
    private val listAdapter: TasksActivityAdapter = TasksActivityAdapter().also {
        it.setOnItemClickListener {position ->
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
        observeTasks() }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViews() {
        setupRecyclerView()
        txtConcept.setOnEditorActionListener { _, actionId, _ ->
            OnEditorAction(actionId)
        }
        imgAddTask.setOnClickListener { addTask() }
    }

    private fun OnEditorAction(actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_DONE) {
            addTask()
            true
        } else {
            false
        }
    }

    private fun addTask() {
       // lblConcept.hideKeyboard()
        if(checkTxt()){
            viewModel.addTask(txtConcept.text.toString())
        }
        txtConcept.setText("")
        observeTasks()


    }

    @SuppressLint("StringFormatInvalid")
    private fun checkTxt(): Boolean {
        return if(txtConcept.text.toString().isBlank()){
            txtConcept.error = getString(R.string.main_invalid_concept)
            false
        }else{
            true
        }
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

    private fun changeTask(position:Int) {
        val task = listAdapter.getItem(position)
        task.completed= !task.completed
        observeTasks()


    }

}


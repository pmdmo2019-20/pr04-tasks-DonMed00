package es.iessaladillo.pedrojoya.pr04.ui.main

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_activity_item.*
import kotlinx.android.synthetic.main.tasks_activity_item.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// TODO: Crea una clase TasksActivityAdapter que actúe como adaptador del RecyclerView
//  y que trabaje con una lista de tareas.
//  Cuando se haga click sobre un elemento se debe cambiar el estado de completitud
//  de la tarea, pasando de completada a pendiente o viceversa.
//  La barra de cada elemento tiene un color distinto dependiendo de si la tarea está
//  completada o no.
//  Debajo del concepto se muestra cuando fue creada la tarea, si la tarea está pendiente,
//  o cuando fue completada si la tarea ya ha sido completada.
//  Si la tarea está completada, el checkBox estará chequeado y el concepto estará tachado.

class TasksActivityAdapter() : RecyclerView.Adapter<TasksActivityAdapter.ViewHolder>() {

    private var data: List<Task> = emptyList()
    private var onItemClickListener : ((Int)->Unit)? = null

    init {
        setHasStableIds(true)
    }

    fun setOnItemClickListener(listener: ((Int)->Unit)?){
        onItemClickListener=listener
    }

    override fun getItemId(position: Int): Long {
        return data[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.tasks_activity_item, parent, false)
        return ViewHolder(itemView,onItemClickListener)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task: Task = data[position]
        holder.bind(task)


    }

    fun submitList(newList: List<Task>) {
        data = newList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Task = data[position]



   class ViewHolder(override val containerView: View,onitemClickListener: ((Int)->Unit)?) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {


        init {
            containerView.setOnClickListener { onitemClickListener?.invoke(adapterPosition) }
        }


        @SuppressLint("ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(task: Task) {
            task.run {
                containerView.lblConcept.text = concept
                containerView.chkCompleted.isChecked = completed
                if (!completed) {
                    containerView.lblCompleted.text = createdAt
                    containerView.viewBar.setBackgroundColor(R.color.colorWhite)

                    // containerView.viewBar.setBackgroundColor(R.color.colorPendingTask)
                } else {
                    val dateTime = LocalDateTime.now()
                    val formatTime: String = dateTime.format(
                        DateTimeFormatter.ofPattern("M/d/y , HH:mm:ss")
                    )
                    val spanBuilder = SpannableStringBuilder(concept)
                    val strikethroughSpan = StrikethroughSpan()
                    spanBuilder.setSpan(
                        strikethroughSpan,  // Span to add
                        0,  // Start
                        concept.length-1,  // End of the span (exclusive)
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Text changes will not reflect in the strike changing
                    )
                    containerView.lblConcept.text = spanBuilder
                    completedAt = "Completed at :$formatTime"
                    containerView.lblCompleted.text = completedAt
                }


            }
        }
    }

    interface OnitemClickListener{
        fun onClick(position: Int)
    }


}

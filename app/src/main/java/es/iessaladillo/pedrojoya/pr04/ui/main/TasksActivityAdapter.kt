package es.iessaladillo.pedrojoya.pr04.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.entity.Task

// TODO: Crea una clase TasksActivityAdapter que actúe como adaptador del RecyclerView
//  y que trabaje con una lista de tareas.
//  Cuando se haga click sobre un elemento se debe cambiar el estado de completitud
//  de la tarea, pasando de completada a pendiente o viceversa.
//  La barra de cada elemento tiene un color distinto dependiendo de si la tarea está
//  completada o no.
//  Debajo del concepto se muestra cuando fue creada la tarea, si la tarea está pendiente,
//  o cuando fue completada si la tarea ya ha sido completada.
//  Si la tarea está completada, el checkBox estará chequeado y el concepto estará tachado.

class TasksActivityAdapter() : RecyclerView.Adapter<TasksActivityAdapter.ViewHolder>(){

    private var data: List<Task> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.tasks_activity_item, parent, false)
        return ViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task: Task = data[position]
        holder.bind(task)


    }

    fun submitList(newList: List<Task>) {
        data = newList
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lblConcept: TextView = itemView.findViewById(R.id.lblConcept)
        val lblCompleted: TextView = itemView.findViewById(R.id.lblCompleted)
        val chkCompleted: CheckBox = itemView.findViewById(R.id.chkCompleted)
       // val viewBar: View = itemView.findViewById(R.id.viewBar)

        fun bind(task: Task) {
            task.run {
                lblConcept.text = concept
                if (!chkCompleted.isChecked) {
                    lblCompleted.text = createdAt
                } else {
                    lblCompleted.text = completedAt
                }


            }
        }


    }

}

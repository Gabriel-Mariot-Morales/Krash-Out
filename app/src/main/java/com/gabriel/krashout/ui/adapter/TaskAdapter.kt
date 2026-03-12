package com.gabriel.krashout.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gabriel.krashout.R
import com.gabriel.krashout.data.local.entity.TaskEntity

// Clase que funciona como puente entre la base de datos y la lista visual de la pantalla
class TaskAdapter(private val onTaskCompleted: (TaskEntity) -> Unit) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    // Clase interna que guarda las referencias a los textos y botones de una tarjeta individual
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textTaskTitle)
        val descTextView: TextView = itemView.findViewById(R.id.textTaskDescription)
        val xpTextView: TextView = itemView.findViewById(R.id.textTaskXp)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkboxTask)

        // Variable que enlaza con la nueva franja de color del XML
        val difficultyStripe: View = itemView.findViewById(R.id.viewDifficultyStripe)

        // Metodo que rellena la tarjeta visual con los datos reales de la tarea
        fun bind(task: TaskEntity) {
            titleTextView.text = task.title
            descTextView.text = task.description
            xpTextView.text = "+${task.xpReward} XP"

            // Asigna el color a la franja dependiendo de los puntos de experiencia que tenga la tarea
            val colorResId = when(task.xpReward) {
                20 -> R.color.diff_very_easy
                40 -> R.color.diff_easy
                60 -> R.color.diff_medium
                80 -> R.color.diff_hard
                100 -> R.color.diff_very_hard
                else -> R.color.wave_cyan
            }
            difficultyStripe.setBackgroundColor(ContextCompat.getColor(itemView.context, colorResId))

            // Quitamos momentaneamente la escucha de la casilla para que no salte por error al reciclar la tarjeta
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = task.isCompleted

            // Ponemos a escuchar la casilla para saber cuando el usuario marca la tarea como terminada
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onTaskCompleted(task)
                }
            }
        }
    }

    // Metodo que infla el diseño XML de la tarjeta cuando el telefono necesita dibujar una nueva en la pantalla
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    // Metodo que conecta una tarea especifica de la lista de datos con una tarjeta que ya esta dibujada
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.bind(currentTask)
    }
}

// Clase que ayuda al adaptador a calcular que tareas han cambiado para hacer animaciones fluidas
class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
    override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
        return oldItem == newItem
    }
}
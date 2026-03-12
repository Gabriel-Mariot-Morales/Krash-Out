package com.gabriel.krashout

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gabriel.krashout.data.local.AppDatabase
import com.gabriel.krashout.data.local.entity.TaskEntity
import com.gabriel.krashout.data.repository.KrashOutRepository
import com.gabriel.krashout.databinding.ActivityMainBinding
import com.gabriel.krashout.ui.adapter.TaskAdapter
import com.gabriel.krashout.ui.viewmodel.MainViewModel
import com.gabriel.krashout.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

// Actividad principal que dibuja la interfaz y conecta los datos con la pantalla
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var taskAdapter: TaskAdapter

    // Variable que contiene todos los elementos visuales de la pantalla principal conectados automaticamente
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflamos la vista usando View Binding y la establecemos como contenido de la pantalla
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instanciamos la base de datos y el repositorio para darselos al ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = KrashOutRepository(database.taskDao(), database.userStatsDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Configuramos la lista reciclable con nuestro adaptador
        taskAdapter = TaskAdapter { task ->
            // Accion que ocurre cuando el usuario marca una tarea como completada y aplica las matematicas
            viewModel.completeTask(task)
        }

        // Le decimos a la lista que se ordene de forma vertical de arriba a abajo usando el binding
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = taskAdapter

        // Observamos el perfil del usuario en tiempo real para actualizar los textos superiores
        lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                if (profile != null) {
                    binding.textUserLevel.text = "Nivel ${profile.currentLevel}"
                    binding.textUserCoins.text = "Monedas: ${profile.virtualCoins}"
                }
            }
        }

        // Observamos la lista de tareas pendientes para dibujarlas en pantalla
        lifecycleScope.launch {
            viewModel.pendingTasks.collect { tasks ->
                taskAdapter.submitList(tasks)
            }
        }

        // Configuramos el boton flotante para abrir el menu de nueva mision al pulsarlo
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    // Funcion que muestra un menu emergente para que el usuario introduzca los datos de su nueva tarea
    // Funcion que muestra un menu emergente para que el usuario introduzca los datos de su nueva tarea
    // Funcion que muestra un menu emergente para que el usuario introduzca los datos de su nueva tarea
    private fun showAddTaskDialog() {
        // Inflamos el diseño del menu usando su propia clase de View Binding
        val dialogBinding = com.gabriel.krashout.databinding.DialogAddTaskBinding.inflate(layoutInflater)

        // Agrupamos los 5 textos de la barra de dificultad para manejarlos mas facilmente
        val textViews = listOf(
            dialogBinding.tvDiffVeryEasy,
            dialogBinding.tvDiffEasy,
            dialogBinding.tvDiffMedium,
            dialogBinding.tvDiffHard,
            dialogBinding.tvDiffVeryHard
        )
        var selectedXp = 0

        // Funcion interna que ilumina el seleccionado y apaga los demas con transparencia
        fun updateSegmentSelection(selectedTextView: android.widget.TextView, xp: Int) {
            selectedXp = xp
            textViews.forEach { it.alpha = 0.3f } // Volvemos todos semitransparentes (apagados)
            selectedTextView.alpha = 1.0f // Hacemos solido y brillante solo el que se ha tocado
        }

        // Por defecto, ponemos todos semitransparentes hasta que el usuario elija
        textViews.forEach { it.alpha = 0.3f }

        // Asignamos la puntuacion y el brillo a cada segmento al ser pulsado
        dialogBinding.tvDiffVeryEasy.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffVeryEasy, 20) }
        dialogBinding.tvDiffEasy.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffEasy, 40) }
        dialogBinding.tvDiffMedium.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffMedium, 60) }
        dialogBinding.tvDiffHard.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffHard, 80) }
        dialogBinding.tvDiffVeryHard.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffVeryHard, 100) }

        // Creamos la ventana emergente y le incrustamos la raiz visual de nuestro binding
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Añadir") { _, _ ->
                val title = dialogBinding.editTaskTitle.text.toString()
                val description = dialogBinding.editTaskDescription.text.toString()

                if (title.isNotEmpty() && selectedXp > 0) {
                    val newTask = TaskEntity(
                        title = title,
                        description = description,
                        xpReward = selectedXp,
                        isCompleted = false,
                        assignedDateTimestamp = System.currentTimeMillis()
                    )
                    viewModel.addTask(newTask)
                } else {
                    android.widget.Toast.makeText(this, "Falta el título o elegir una dificultad", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
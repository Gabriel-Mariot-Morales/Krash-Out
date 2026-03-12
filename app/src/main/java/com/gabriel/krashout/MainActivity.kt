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
            // Accion que ocurre cuando el usuario marca una tarea como completada
            val completedTask = task.copy(isCompleted = true)
            viewModel.updateTask(completedTask)
            viewModel.addVirtualCoins(10)
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
    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.editTaskTitle)
        val editDescription = dialogView.findViewById<EditText>(R.id.editTaskDescription)

        // Enlazamos los 5 botones de dificultad
        val btnVp = dialogView.findViewById<android.widget.Button>(R.id.btnDiffVeryEasy)
        val btnEasy = dialogView.findViewById<android.widget.Button>(R.id.btnDiffEasy)
        val btnMed = dialogView.findViewById<android.widget.Button>(R.id.btnDiffMedium)
        val btnHard = dialogView.findViewById<android.widget.Button>(R.id.btnDiffHard)
        val btnVh = dialogView.findViewById<android.widget.Button>(R.id.btnDiffVeryHard)

        val buttons = listOf(btnVp, btnEasy, btnMed, btnHard, btnVh)
        var selectedXp = 0

        // Funcion interna que oscurece los botones no seleccionados para destacar el elegido
        fun updateButtonSelection(selectedButton: android.widget.Button, xp: Int) {
            selectedXp = xp
            buttons.forEach { it.alpha = 0.3f } // Volvemos todos semitransparentes
            selectedButton.alpha = 1.0f // Hacemos solido solo el que se ha tocado
        }

        // Por defecto, ponemos todos semitransparentes hasta que el usuario elija
        buttons.forEach { it.alpha = 0.3f }

        // Asignamos la puntuacion a cada boton al ser pulsado
        btnVp.setOnClickListener { updateButtonSelection(btnVp, 20) }
        btnEasy.setOnClickListener { updateButtonSelection(btnEasy, 40) }
        btnMed.setOnClickListener { updateButtonSelection(btnMed, 60) }
        btnHard.setOnClickListener { updateButtonSelection(btnHard, 80) }
        btnVh.setOnClickListener { updateButtonSelection(btnVh, 100) }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Añadir") { _, _ ->
                val title = editTitle.text.toString()
                val description = editDescription.text.toString()

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
                    Toast.makeText(this, "Falta el título o elegir una dificultad", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
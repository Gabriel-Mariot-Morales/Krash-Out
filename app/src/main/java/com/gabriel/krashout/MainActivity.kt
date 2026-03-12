package com.gabriel.krashout

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabriel.krashout.data.local.AppDatabase
import com.gabriel.krashout.data.local.entity.TaskEntity
import com.gabriel.krashout.data.repository.KrashOutRepository
import com.gabriel.krashout.ui.adapter.TaskAdapter
import com.gabriel.krashout.ui.viewmodel.MainViewModel
import com.gabriel.krashout.ui.viewmodel.MainViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

// Actividad principal que dibuja la interfaz y conecta los datos con la pantalla
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instanciamos la base de datos y el repositorio para darselos al ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = KrashOutRepository(database.taskDao(), database.userStatsDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Enlazamos los elementos visuales de la pantalla con variables de Kotlin
        val textUserLevel = findViewById<TextView>(R.id.textUserLevel)
        val textUserCoins = findViewById<TextView>(R.id.textUserCoins)
        val recyclerViewTasks = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)

        // Configuramos la lista reciclable con nuestro adaptador
        taskAdapter = TaskAdapter { task ->
            // Accion que ocurre cuando el usuario marca una tarea como completada
            val completedTask = task.copy(isCompleted = true)
            viewModel.updateTask(completedTask)
            viewModel.addVirtualCoins(10)
        }

        // Le decimos a la lista que se ordene de forma vertical de arriba a abajo
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter

        // Observamos el perfil del usuario en tiempo real para actualizar los textos superiores
        lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                if (profile != null) {
                    textUserLevel.text = "Nivel ${profile.currentLevel}"
                    textUserCoins.text = "Monedas: ${profile.virtualCoins}"
                }
            }
        }

        // Observamos la lista de tareas pendientes para dibujarlas en pantalla
        lifecycleScope.launch {
            viewModel.pendingTasks.collect { tasks ->
                taskAdapter.submitList(tasks)
            }
        }

        // Configuramos el boton flotante para crear una tarea de prueba de tematica maritima al pulsarlo
        fabAddTask.setOnClickListener {
            val newTask = TaskEntity(
                title = "Limpiar la cubierta",
                description = "Fregar el suelo del barco para evitar resbalones",
                xpReward = 50,
                isCompleted = false,
                assignedDateTimestamp = System.currentTimeMillis()
            )
            viewModel.addTask(newTask)
        }
    }
}
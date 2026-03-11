package com.gabriel.krashout.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabriel.krashout.data.local.entity.TaskEntity
import com.gabriel.krashout.data.local.entity.UserStatsEntity
import com.gabriel.krashout.data.repository.KrashOutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ViewModel que gestiona la logica de la interfaz y se comunica con el Repositorio
class MainViewModel(private val repository: KrashOutRepository) : ViewModel() {

    // Expone el flujo de tareas pendientes para que la pantalla las observe reactivamente
    val pendingTasks: Flow<List<TaskEntity>> = repository.getPendingTasks()

    // Expone el flujo constante con los datos del perfil del usuario
    val userProfile: Flow<UserStatsEntity?> = repository.getUserProfile()

    // Ejecuta la insercion de una nueva tarea aislada en un hilo secundario (IO)
    fun addTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    // Actualiza el estado de una tarea en la base de datos aislando el proceso
    fun updateTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    // Suma monedas al perfil del usuario sin bloquear el hilo principal
    fun addVirtualCoins(amount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addVirtualCoins(amount)
        }
    }
}

// Fabrica constructora necesaria para poder inyectar el Repositorio dentro del ViewModel
class MainViewModelFactory(private val repository: KrashOutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
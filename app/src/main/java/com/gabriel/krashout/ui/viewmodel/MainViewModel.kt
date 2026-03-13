package com.gabriel.krashout.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabriel.krashout.data.local.entity.TaskEntity
import com.gabriel.krashout.data.local.entity.UserStatsEntity
import com.gabriel.krashout.data.repository.KrashOutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.sqrt

// ViewModel que gestiona la logica de la interfaz y se comunica con el Repositorio
class MainViewModel(private val repository: KrashOutRepository) : ViewModel() {

    // Expone el flujo de tareas pendientes para que la pantalla las observe reactivamente
    val pendingTasks: Flow<List<TaskEntity>> = repository.getPendingTasks()

    // Expone el flujo constante con los datos del perfil del usuario
    val userProfile: Flow<UserStatsEntity?> = repository.getUserProfile()

    // Bloque de inicializacion que se ejecuta al crear el ViewModel
    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserProfile().collect { profile ->
                if (profile == null) {
                    repository.initializeProfileIfNeeded()
                }
            }
        }
    }

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

    // Funcion maestra que procesa una tarea completada, aplica las matematicas de XP y sube de nivel
    fun completeTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Marcamos la tarea como completada y la guardamos
            val completedTask = task.copy(isCompleted = true)
            repository.updateTask(completedTask)

            // 2. Leemos la foto exacta de como esta el perfil del jugador en este momento
            val currentProfile = repository.getUserProfile().firstOrNull()

            if (currentProfile != null) {
                // Sumamos la experiencia de esta tarea al total que ya tenia
                val newTotalXp = currentProfile.totalXp + task.xpReward

                // Aplicamos la formula RPG: raiz cuadrada de la XP dividida entre 10, mas el nivel 1 base
                val newLevel = (sqrt(newTotalXp.toDouble()) / 10).toInt() + 1

                // Recompensamos con monedas (el 10% de la XP de la tarea)
                val coinsEarned = task.xpReward / 10
                val newCoins = currentProfile.virtualCoins + coinsEarned

                // Sobrescribimos el perfil con los nuevos calculos matematicos
                val updatedProfile = currentProfile.copy(
                    totalXp = newTotalXp,
                    currentLevel = newLevel,
                    virtualCoins = newCoins
                )

                // Guardamos el perfil evolucionado en la base de datos
                repository.saveUserProfile(updatedProfile)
            }
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
package com.gabriel.krashout.data.repository

import com.gabriel.krashout.data.local.dao.TaskDao
import com.gabriel.krashout.data.local.dao.UserStatsDao
import com.gabriel.krashout.data.local.entity.TaskEntity
import com.gabriel.krashout.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Clase intermediaria (Repositorio) que gestiona el acceso a los datos.
 * Es la unica clase que se comunicara con los DAOs, aislando la logica de base de datos del resto de la app.
 */
class KrashOutRepository(
    private val taskDao: TaskDao,
    private val userStatsDao: UserStatsDao
) {

    // --- SECCION DE TAREAS ---

    // Obtiene un flujo constante con todas las tareas almacenadas
    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    // Obtiene un flujo solo con las tareas que aun no se han completado
    fun getPendingTasks(): Flow<List<TaskEntity>> {
        return taskDao.getPendingTasks()
    }

    // Guarda una nueva tarea en la base de datos
    fun addTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    // Actualiza el estado de una tarea (por ejemplo, cuando se marca como completada)
    fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    // --- SECCION DE PERFIL DE USUARIO ---

    // Obtiene un flujo constante con los datos en tiempo real del usuario
    fun getUserProfile(): Flow<UserStatsEntity?> {
        return userStatsDao.getUserStats()
    }

    // Guarda los datos del usuario o los sobrescribe si ya existia el perfil (id=1)
    fun saveUserProfile(profile: UserStatsEntity) {
        userStatsDao.insertOrUpdateUserStats(profile)
    }

    // Suma la cantidad indicada al saldo de monedas virtuales
    fun addVirtualCoins(amount: Int) {
        userStatsDao.addCoins(amount)
    }
}
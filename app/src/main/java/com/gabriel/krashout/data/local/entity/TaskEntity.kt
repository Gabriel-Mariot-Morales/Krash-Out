package com.gabriel.krashout.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Clase que representa una tarea individual que el usuario debe completar
@Entity(tableName = "tasks")
data class TaskEntity(
    // Identificador unico generado automaticamente para cada tarea
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Titulo principal de la tarea
    val title: String,

    // Descripcion detallada de lo que se debe hacer
    val description: String,

    // Cantidad de experiencia que el usuario gana al completarla
    val xpReward: Int,

    // Indicador de si la tarea ya ha sido terminada
    val isCompleted: Boolean = false,

    // Fecha en la que se debe realizar la tarea guardada en milisegundos
    val assignedDateTimestamp: Long,

    // Identificador de la rutina que creo esta tarea en caso de que sea repetitiva
    val parentRoutineId: Int? = null
)

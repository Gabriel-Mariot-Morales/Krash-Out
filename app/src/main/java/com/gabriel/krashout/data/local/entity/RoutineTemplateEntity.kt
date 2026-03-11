package com.gabriel.krashout.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Clase que funciona como molde para generar tareas repetitivas automaticamente
@Entity(tableName = "routine_templates")
data class RoutineTemplateEntity(
    // Identificador unico generado automaticamente para cada rutina
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Nombre de la rutina
    val title: String,

    // Texto que define que dias se repite la rutina
    val repetitionPattern: String,

    // Nivel de dificultad base para calcular la experiencia que otorgara
    val baseDifficulty: Int,

    // Indicador de si la rutina esta funcionando o pausada por el usuario
    val isActive: Boolean = true
)

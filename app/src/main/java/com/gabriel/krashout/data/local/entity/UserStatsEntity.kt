package com.gabriel.krashout.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Clase que representa la tabla del perfil del usuario en la base de datos
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    // Identificador estatico para asegurar que solo exista un perfil
    @PrimaryKey
    val id: Int = 1,

    // Puntuacion total historica del usuario
    val totalXp: Int,

    // Nivel actual basado en la experiencia
    val currentLevel: Int,

    // Saldo para comprar en la tienda
    val virtualCoins: Int,

    // Experiencia conseguida durante el dia actual
    val dailyXpAccumulated: Int,

    // Momento del ultimo acceso guardado en formato de milisegundos
    val lastLoginTimestamp: Long
)
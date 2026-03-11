package com.gabriel.krashout.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Clase que guarda los objetos cosmeticos que se pueden comprar en la tienda
@Entity(tableName = "shop_items")
data class ShopItemEntity(
    // Identificador unico del objeto
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Nombre del objeto cosmetico
    val itemName: String,

    // Ruta donde se guarda el dibujo del objeto en el movil
    val assetPath: String,

    // Precio del objeto en monedas virtuales
    val cost: Int,

    // Numero que indica en que hueco de la tienda aparece
    val rotationSlotId: Int,

    // Indicador de si el usuario ya ha comprado este objeto
    val isUnlocked: Boolean = false
)
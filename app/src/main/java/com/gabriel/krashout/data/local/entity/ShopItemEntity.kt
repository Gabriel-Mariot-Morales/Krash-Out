package com.gabriel.krashout.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Int,
    val iconEmoji: String, // Usaremos emojis temporalmente como iconos
    val category: String, // "HEAD", "TOP", "BOT"
    val isPurchased: Boolean = false,
    val rotationSlot: Int = 0
)
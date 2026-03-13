package com.gabriel.krashout.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gabriel.krashout.data.local.entity.ShopItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    @Query("SELECT * FROM shop_items WHERE category = :category")
    fun getItemsByCategory(category: String): Flow<List<ShopItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShopItems(items: List<ShopItemEntity>)

    @Update
    fun updateShopItem(item: ShopItemEntity)

    @Query("SELECT COUNT(*) FROM shop_items")
    fun getItemsCount(): Int
}
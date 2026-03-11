package com.gabriel.krashout.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabriel.krashout.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

// Interfaz que expone las acciones permitidas sobre el perfil del usuario
@Dao
interface UserStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStatsEntity)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Query("UPDATE user_stats SET virtualCoins = virtualCoins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)
}
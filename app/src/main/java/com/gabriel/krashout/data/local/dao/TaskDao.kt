package com.gabriel.krashout.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gabriel.krashout.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

// Interfaz que expone las acciones permitidas sobre las tareas
@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: TaskEntity)

    @Update
    fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY assignedDateTimestamp ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY assignedDateTimestamp ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>
}
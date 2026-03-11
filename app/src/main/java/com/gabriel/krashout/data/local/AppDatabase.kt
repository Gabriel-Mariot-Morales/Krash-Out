package com.gabriel.krashout.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabriel.krashout.data.local.dao.TaskDao
import com.gabriel.krashout.data.local.dao.UserStatsDao
import com.gabriel.krashout.data.local.entity.RoutineTemplateEntity
import com.gabriel.krashout.data.local.entity.ShopItemEntity
import com.gabriel.krashout.data.local.entity.TaskEntity
import com.gabriel.krashout.data.local.entity.UserStatsEntity

// Clase maestra que construye el archivo fisico de la base de datos
@Database(
    entities = [
        UserStatsEntity::class,
        TaskEntity::class,
        RoutineTemplateEntity::class,
        ShopItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Metodo para acceder a las operaciones del perfil
    abstract fun userStatsDao(): UserStatsDao

    // Metodo para acceder a las operaciones de las tareas
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Metodo que crea o devuelve la base de datos asegurando que no haya duplicados
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "krashout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.poetry.shijian.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.poetry.shijian.data.local.entities.DailyRecEntity
import com.poetry.shijian.data.local.entities.FavoriteEntity
import com.poetry.shijian.data.local.entities.PoemEntity
import com.poetry.shijian.data.local.entities.ReadHistoryEntity

@Database(
    entities = [
        PoemEntity::class,
        FavoriteEntity::class,
        ReadHistoryEntity::class,
        DailyRecEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun poemDao(): PoemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shijian.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

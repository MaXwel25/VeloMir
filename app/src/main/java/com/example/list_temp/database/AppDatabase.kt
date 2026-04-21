package com.example.list_temp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.list_temp.data.BikeType
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.data.BikeModel

@Database(
    entities = [BikeType::class, Manufacturer::class, BikeModel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bikeTypeDao(): BikeTypeDao
    abstract fun manufacturerDao(): ManufacturerDao
    abstract fun bikeModelDao(): BikeModelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "velomir_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
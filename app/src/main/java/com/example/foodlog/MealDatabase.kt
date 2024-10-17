package com.example.foodlog.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodlog.data.Meal
import com.example.foodlog.data.MealDao

@Database(entities = [Meal::class], version = 3, exportSchema = false)
abstract class MealDatabase : RoomDatabase() {

    // Reference to the DAO
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null
        // Singleton to prevent multiple instances of the database being opened at the same time
        fun getDatabase(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
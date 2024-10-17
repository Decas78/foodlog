package com.example.foodlog.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    // Get all meals, observing changes in the data
    @Query("SELECT * FROM meals ORDER BY id DESC")
    fun getAllMeals(): Flow<List<Meal>>

    // Insert a new meal into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)

    // Update the meal's photo URI
    @Query("UPDATE meals SET imageUri = :imageUri WHERE id = :mealId")
    suspend fun updateMealPhotoURI(mealId: Long, imageUri: String)

    // Update the meal's photo URI
    @Query("UPDATE meals SET imageUrl = :imageUrl WHERE id = :mealId")
    suspend fun updateMealPhotoURL(mealId: Long, imageUrl: String)

    // Delete a meal from the database (optional)
    @Delete
    suspend fun deleteMeal(meal: Meal)

}
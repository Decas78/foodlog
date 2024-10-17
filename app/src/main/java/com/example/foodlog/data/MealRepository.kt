package com.example.foodlog.data

import android.app.Application
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.Flow
import com.example.foodlog.api.ApiService
import com.example.foodlog.api.NutritionalInfo
import com.example.foodlog.database.MealDatabase
import com.example.foodlog.firebase.FirebaseStorageManager

class MealRepository private constructor(
    private val mealDao: MealDao,
    private val apiService: ApiService,
    private val firebaseStorageManager: FirebaseStorageManager
) {
    companion object {
        @Volatile private var instance: MealRepository? = null

        fun getInstance(application: Application): MealRepository {
            return instance ?: synchronized(this) {
                instance ?: MealRepository(
                    MealDatabase.getDatabase(application).mealDao(),
                    ApiService.create(),
                    FirebaseStorageManager()
                ).also { instance = it }
            }
        }
    }

    // Fetch all meals from the local database
    fun getAllMeals(): Flow<List<Meal>> {
        return mealDao.getAllMeals()
    }

    // Add a meal to the database
    suspend fun addMeal(meal: Meal) {
        mealDao.insertMeal(meal)
        Log.d("MealRepository", "Meal added: $meal")
    }

    // Fetch nutritional information from Calorie Ninjas API
    suspend fun getNutritionalInfo(foodName: String, portionSize: String): NutritionalInfo? {
        return try {
            Log.d("MealRepository", "Fetching nutritional info for: portionSize='$portionSize', foodName='$foodName'")
            val query = " $portionSize grams of $foodName"
            apiService.searchFood(query)?.items?.firstOrNull()  // Get the first result, or null if none
        } catch (e: Exception) {
            Log.e("MealRepository", "Error fetching nutritional info: ${e.message}")
            null // Handle error, return null if API call fails
        }
    }

    // Upload a meal photo to Firebase and link it to the meal
    // TODO Ensure meals have ids that can be used
    suspend fun uploadPhoto(photoUri: Uri, mealId: Long) {
        val photoUrl = firebaseStorageManager.uploadPhoto(photoUri)
        mealDao.updateMealPhotoURL(mealId, photoUrl)
    }
}
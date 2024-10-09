package com.example.foodlog

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.foodlog.api.NutritionalInfo
import com.example.foodlog.data.Meal
import com.example.foodlog.data.MealRepository
import kotlinx.coroutines.launch

class MealViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for observing meals from the Room database
    private val mealRepository by lazy { MealRepository.getInstance(application) }
    val meals: LiveData<List<Meal>> = mealRepository.getAllMeals().asLiveData()

    // Method to add a new meal
    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.addMeal(meal)
        }
    }

    // Method to fetch nutritional info from the API
    fun getNutritionalInfo(foodName: String, portionSize: String): LiveData<NutritionalInfo?> {
        return liveData {
            val result = mealRepository.getNutritionalInfo(foodName, portionSize)
            emit(result)
        }
    }

    // Method to upload meal photo to Firebase and associate it with a meal
    fun uploadMealPhoto(photoUri: Uri, mealId: Long) {
        viewModelScope.launch {
            try {
                mealRepository.uploadPhoto(photoUri, mealId)
            } catch (e: Exception) {
                // Handle error, e.g., log error or notify user
            }
        }
    }

    // Factory class to instantiate the MealViewModel with MealRepository
}
class MealViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
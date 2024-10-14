package com.example.foodlog.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    // API call to search for food nutritional information
    @Headers("X-Api-Key: C0AvnCO3EKcJ20BY1iihpA==vVOteKb6M4MnQdsu")  // Replace with your actual API key
    @GET("v1/nutrition")
    suspend fun searchFood(
        @Query("query") query: String
    ): ApiResponse?

    companion object {
        private const val BASE_URL = "https://api.calorieninjas.com/" // CalorieNinjas base URL

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}

// Data class for API response
data class ApiResponse(
    val items: List<NutritionalInfo>
)

// Data class for storing nutritional information retrieved from the API
data class NutritionalInfo(
    val calories: Double,
    val carbohydrates_total_g: Double,
    val cholesterol_mg: Int,
    val fat_saturated_g: Double,
    val fat_total_g: Double,
    val fiber_g: Double,
    val name: String,
    val potassium_mg: Int,
    val protein_g: Double,
    val serving_size_g: Double,
    val sodium_mg: Int,
    val sugar_g: Double
)
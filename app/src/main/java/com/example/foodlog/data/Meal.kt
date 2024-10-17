package com.example.foodlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val portionSize: Int,
    val calories: Int,
    val carbs: Int,
    val fats: Int,
    val protein: Int,
    val mealType: String,
    val imageUri: String?, //  image URI
    val imageUrl: String? // image URL
)
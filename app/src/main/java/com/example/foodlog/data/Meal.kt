package com.example.foodlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val portionSize: Int,
    val calories: Int,
    val mealType: String,
    val photoUrl: String? = null // Nullable field for the photo URL
)
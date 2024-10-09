package com.example.foodlog

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodlog.databinding.ActivityFoodLogBinding

class FoodLogActivity : AppCompatActivity() {

    private val mealViewModel: MealViewModel by viewModels {
        MealViewModelFactory(application)
    }
    private lateinit var binding: ActivityFoodLogBinding


    private lateinit var mealAdapter: MealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding
        binding = ActivityFoodLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        mealAdapter = MealAdapter()
        binding.mealRecyclerView.apply {
            adapter = mealAdapter
            layoutManager = LinearLayoutManager(this@FoodLogActivity)
        }

        // Observe the LiveData from the ViewModel
        mealViewModel.meals.observe(this) { meals ->
            mealAdapter.submitList(meals)
            updateCalorieSummary(meals.sumOf { it.calories })
        }
    }

    // Update the calorie summary display
    private fun updateCalorieSummary(totalCalories: Int) {
        val dailyGoal = 2000 // Example calorie goal
        binding.calorieSummaryTextView.text = "Calories: $totalCalories / $dailyGoal kcal"
    }
}
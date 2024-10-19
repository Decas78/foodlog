package com.example.foodlog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodlog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // click listener to navigate to FoodLogActivity
        binding.viewFoodLogButton.setOnClickListener {
            val intent = Intent(this, FoodLogActivity::class.java)
            startActivity(intent)
        }

        // click listener to navigate to AddMealActivity
        binding.logMealButton.setOnClickListener {
            val intent = Intent(this, AddMealActivity::class.java)
            startActivity(intent)
        }
    }
}
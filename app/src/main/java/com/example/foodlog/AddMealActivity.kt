package com.example.foodlog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.foodlog.data.Meal
import com.example.foodlog.databinding.ActivityAddMealBinding

class AddMealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMealBinding
    private var selectedImageUri: Uri? = null

    // ViewModel for managing meal addition and API calls
    private val mealViewModel: MealViewModel by viewModels()

    // Firebase
    private val photoUploadManager = PhotoUploadManager()

    // Register activity result for selecting an image from the gallery
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.mealPhotoImageView.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "Upload Photo" button click
        binding.uploadPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            selectImageLauncher.launch(intent)
        }

        // "Save Meal" button click
        binding.btnSaveMeal.setOnClickListener {
            val mealName = binding.etMealName.text.toString()
            val portionSize = binding.etPortionSize.text.toString().toIntOrNull() ?: 0
            val calories = binding.etCalories.text.toString().toIntOrNull() ?: 0

            if (mealName.isNotBlank() && calories > 0) {
                // Upload photo if selected
                if (selectedImageUri != null) {
                    photoUploadManager.uploadPhoto(
                        imageUri = selectedImageUri!!,
                        onSuccess = { photoUrl ->
                            // Create and save new meal entry with photo URL
                            saveMealToDatabase(mealName, portionSize, calories, photoUrl)
                        },
                        onFailure = { exception ->
                            Toast.makeText(this, "Failed to upload photo: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    // Save meal without a photo
                    saveMealToDatabase(mealName, portionSize, calories, null)
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle API search button click
        binding.btnSearchFood.setOnClickListener {
            val foodName = binding.etMealName.text.toString()
            val portionSize = binding.etPortionSize.text.toString()
            if (foodName.isNotBlank()) {
                mealViewModel.getNutritionalInfo(foodName, portionSize).observe(this) { nutritionalInfo ->
                    if (nutritionalInfo != null) {
                        // Pre-fill the calories field based on API result
                        binding.etCalories.setText(nutritionalInfo.calories.toInt().toString())
                        //TODO add more fields here from nutritionalinfo
                    } else {
                        Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Save the meal to the database
    private fun saveMealToDatabase(mealName: String, portionSize: Int, calories: Int, photoUrl: String?) {
        val meal = Meal(
            name = mealName,
            portionSize = portionSize,
            calories = calories,
            mealType = getSelectedMealType(),
            photoUrl = photoUrl // Save photo URL or null if no photo is uploaded
        )
        mealViewModel.addMeal(meal)
        Toast.makeText(this, "Meal added", Toast.LENGTH_SHORT).show()
        finish() // Close the activity
    }

    // Get the selected meal type (e.g., breakfast, lunch, dinner, snack)
    private fun getSelectedMealType(): String {
        return when (binding.rgMealType.checkedRadioButtonId) {
            R.id.rbBreakfast -> "Breakfast"
            R.id.rbLunch -> "Lunch"
            R.id.rbDinner -> "Dinner"
            else -> "Snack"
        }
    }
}
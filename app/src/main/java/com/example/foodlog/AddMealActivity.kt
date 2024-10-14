package com.example.foodlog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide // Make sure to add Glide dependency
import com.example.foodlog.data.Meal
import com.example.foodlog.databinding.ActivityAddMealBinding

class AddMealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMealBinding
    private var selectedImageUri: Uri? = null

    // ViewModel for managing meal addition and API calls
    private val mealViewModel: MealViewModel by viewModels()

    // Request code for image selection using SAF
    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "Upload Photo" button click
        binding.uploadPhotoButton.setOnClickListener {
            // Use SAF to select an image
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }

        // "Save Meal" button click
        binding.btnSaveMeal.setOnClickListener {
            val mealName = binding.etMealName.text.toString()
            val portionSize = binding.etPortionSize.text.toString().toIntOrNull() ?: 0
            val calories = binding.etCalories.text.toString().toIntOrNull() ?: 0

            if (mealName.isNotBlank() && calories > 0) {
                // Save meal with selected image URI or without a photo
                saveMealToDatabase(mealName, portionSize, calories, selectedImageUri)
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
                        // TODO: Add more fields here from nutritionalInfo if needed
                    } else {
                        Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Handle the result from the image picker (SAF)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // Persist access permissions for future use
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                // Use the content URI to load the image with Glide or set the image URI to ImageView
                Glide.with(this)
                    .load(uri)
                    .into(binding.mealPhotoImageView)

                // Save the selected URI for saving in the database
                selectedImageUri = uri
            }
        }
    }


    // Save the meal to the local database
    private fun saveMealToDatabase(mealName: String, portionSize: Int, calories: Int, photoUri: Uri?) {
        val meal = Meal(
            name = mealName,
            portionSize = portionSize,
            calories = calories,
            mealType = getSelectedMealType(),
            imageUri = photoUri?.toString() // Save the image URI as a string
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

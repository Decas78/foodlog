package com.example.foodlog

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.foodlog.data.Meal
import com.example.foodlog.databinding.ActivityAddMealBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class AddMealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMealBinding
    private var selectedImageUri: Uri? = null
    private var imageUrl: String? = null

    // ViewModel for managing meal addition and API calls
    private val mealViewModel: MealViewModel by viewModels()

    // Request codes
    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
        private const val TAKE_IMAGE_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "Upload Photo" button click
        binding.uploadPhotoButton.setOnClickListener {
            showImageSourceDialog()
        }

        // "Save Meal" button click
        binding.btnSaveMeal.setOnClickListener {
            val mealName = binding.etMealName.text.toString()
            val portionSize = binding.etPortionSize.text.toString().toIntOrNull() ?: 0
            val calories = binding.etCalories.text.toString().toIntOrNull() ?: 0
            val carbs = binding.etCarbs.text.toString().toIntOrNull() ?: 0
            val fats = binding.etFats.text.toString().toIntOrNull() ?: 0
            val protein = binding.etProtein.text.toString().toIntOrNull() ?: 0

            if (mealName.isNotBlank() && calories > 0) {
                saveMealToDatabase(mealName, portionSize, calories, carbs, fats, protein, selectedImageUri)
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
                        binding.etCalories.setText(nutritionalInfo.calories.toInt().toString())
                        binding.etCarbs.setText(nutritionalInfo.carbohydrates_total_g.toInt().toString())
                        binding.etFats.setText(nutritionalInfo.fat_total_g.toInt().toString())
                        binding.etProtein.setText(nutritionalInfo.protein_g.toInt().toString())
                    } else {
                        Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Upload Photo", "Take a Photo")
        AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openImagePicker() // Upload Photo
                    1 -> takePhoto() // Take a Photo
                }
            }
            .show()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        handleImageUri(uri)
                    }
                }
            }
            TAKE_IMAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = data?.extras?.get("data") as? Bitmap // Ensure it's cast to Bitmap
                    bitmap?.let {
                        val savedUri = saveBitmapToFile(it, this) // Pass the Bitmap
                        binding.mealPhotoImageView.setImageBitmap(it)
                        selectedImageUri = savedUri
                    }
                }
            }
        }
    }





    private fun handleImageUri(uri: Uri) {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        Glide.with(this)
            .load(uri)
            .into(binding.mealPhotoImageView)

        selectedImageUri = uri
    }

    private fun saveBitmapToFile(bitmap: Bitmap, context: Context): Uri? {
        // Create a ContentValues object to specify the details of the image
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${UUID.randomUUID()}.jpg") // Name of the image
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // MIME type
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // Save to Pictures directory
        }

        // Insert the image into the MediaStore
        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                // Open an output stream to write the bitmap
                context.contentResolver.openOutputStream(it)?.use { outputStream: OutputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null // Return null on error
            }
        }

        return uri // Return the URI of the saved image
    }


    private fun saveMealToDatabase(mealName: String, portionSize: Int, calories: Int, carbs: Int, fats: Int, protein: Int, photoUri: Uri?) {
        if (photoUri != null) {
            uploadImageToFirebase(photoUri) { url ->
                if (url != null) {
                    val meal = Meal(
                        name = mealName,
                        portionSize = portionSize,
                        calories = calories,
                        carbs = carbs,
                        fats = fats,
                        protein = protein,
                        mealType = getSelectedMealType(),
                        imageUri = photoUri.toString(),
                        imageUrl = url
                    )
                    mealViewModel.addMeal(meal)
                    Toast.makeText(this, "Meal added", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getSelectedMealType(): String {
        return when (binding.rgMealType.checkedRadioButtonId) {
            R.id.rbBreakfast -> "Breakfast"
            R.id.rbLunch -> "Lunch"
            R.id.rbDinner -> "Dinner"
            else -> "Snack"
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, callback: (String?) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val fileName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$fileName")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }.addOnFailureListener {
                    callback(null) // If there's an error getting the URL
                }
            }
            .addOnFailureListener {
                callback(null) // Handle error
            }
    }



}

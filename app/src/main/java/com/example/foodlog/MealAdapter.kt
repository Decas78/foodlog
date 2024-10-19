package com.example.foodlog

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.foodlog.data.Meal
import com.example.foodlog.databinding.ItemMealBinding
import javax.sql.DataSource

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var meals: List<Meal> = listOf()

    inner class MealViewHolder(private val binding: ItemMealBinding) : RecyclerView.ViewHolder(binding.root) {

        // Bind meal data to the views
        fun bind(meal: Meal) {
            binding.tvMealName.text = meal.name
            binding.tvCalories.text = "${meal.calories} kcal"
            binding.tvCarbs.text = "Carbohydrates ${meal.carbs} g"
            binding.tvFats.text = "Fats ${meal.fats} g"
            binding.tvProtein.text = "Protein ${meal.protein} g"
            binding.tvMealType.text = meal.mealType

            // Check for internet connectivity before loading the remote image
            if (isNetworkAvailable(binding.root.context)) {
                // Load the image from the Firebase URL if available
                if (!meal.imageUrl.isNullOrEmpty()) {
                    Glide.with(binding.ivMealPhoto.context)
                        .load(meal.imageUrl)
                        .placeholder(R.drawable.ic_add_default) // Placeholder while loading
                        .into(binding.ivMealPhoto)
                } else {
                    // Fallback to loading image from the local URI
                    meal.imageUri?.let { uriString ->
                        val imageUri = Uri.parse(uriString) // Convert the imageUri string back to a Uri
                        Glide.with(binding.ivMealPhoto.context)
                            .load(imageUri)
                            .placeholder(R.drawable.ic_add_default) // Placeholder while loading
                            .into(binding.ivMealPhoto)
                    } ?: run {
                        // Set a default image if no imageUri is available
                        binding.ivMealPhoto.setImageResource(R.drawable.ic_add_default)
                    }
                }
            } else {
                // If no internet connection, load the local image directly
                meal.imageUri?.let { uriString ->
                    val imageUri = Uri.parse(uriString) // Convert the imageUri string back to a Uri
                    Glide.with(binding.ivMealPhoto.context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_add_default) // Placeholder while loading
                        .into(binding.ivMealPhoto)
                } ?: run {
                    // Set a default image if no imageUri is available
                    binding.ivMealPhoto.setImageResource(R.drawable.ic_add_default)
                }
            }
        }


    // Function to check for internet connectivity
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            if (network != null) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

                if (networkCapabilities != null) {
                    return networkCapabilities != null && networkCapabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI)
                            ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                }

            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        return false

    }

    private fun loadLocalImage(meal: Meal) {
            meal.imageUri?.let { uriString ->
                val imageUri = Uri.parse(uriString) // Convert the imageUri string back to a Uri
                Glide.with(binding.ivMealPhoto.context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_add_default) // Placeholder while loading
                    .into(binding.ivMealPhoto)
            } ?: run {
                // Set a default image if no imageUri is available
                binding.ivMealPhoto.setImageResource(R.drawable.ic_add_default)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size

    fun submitList(newMeals: List<Meal>) {
        meals = newMeals
        notifyDataSetChanged() // Ideally, use DiffUtil for better performance.
    }
}

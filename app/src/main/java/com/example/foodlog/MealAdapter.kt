package com.example.foodlog

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Make sure to add Glide dependency if you use it
import com.example.foodlog.data.Meal
import com.example.foodlog.databinding.ItemMealBinding

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

            // Load the image from the Firebase URL if available
            if (!meal.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.ivMealPhoto.context)
                    .load(meal.imageUrl)
                    .placeholder(R.drawable.ic_add_default) // Placeholder while loading
                    .error(R.drawable.ic_error_placeholder)  // Error image if load fails
                    .into(binding.ivMealPhoto)
            } else {
                // Fallback to loading image from the local URI
                meal.imageUri?.let { uriString ->
                    val imageUri = Uri.parse(uriString) // Convert the imageUri string back to a Uri
                    Glide.with(binding.ivMealPhoto.context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_add_default) // Placeholder while loading
                        .error(R.drawable.ic_error_placeholder)  // Error image if load fails
                        .into(binding.ivMealPhoto)
                } ?: run {
                    // Set a default image if no imageUri is available
                    binding.ivMealPhoto.setImageResource(R.drawable.ic_add_default)
                }
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

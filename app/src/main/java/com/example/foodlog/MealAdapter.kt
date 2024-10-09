package com.example.foodlog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Make sure to add Glide dependency if you use it
import com.example.foodlog.data.Meal
import com.example.foodlog.databinding.ItemMealBinding

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var meals: List<Meal> = listOf()

    // ViewHolder class for the RecyclerView
    inner class MealViewHolder(private val binding: ItemMealBinding) : RecyclerView.ViewHolder(binding.root) {

        // Bind meal data to the views
        fun bind(meal: Meal) {
            binding.tvMealName.text = meal.name
            binding.tvCalories.text = "${meal.calories} kcal"
            binding.tvMealType.text = meal.mealType

            // Load photo (if available) using Glide
            meal.photoUrl?.let { url ->
                Glide.with(binding.ivMealPhoto.context)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background) // NEEDS REPLACEMENT TODO
                    .error(R.drawable.ic_error_placeholder) // Error image
                    .into(binding.ivMealPhoto)
            } ?: run {
                // Set a placeholder or default image if no URL is provided
                binding.ivMealPhoto.setImageResource(R.drawable.ic_launcher_background) // NEEDS REPLACEMENT TODO
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

    // Submit the list of meals
    fun submitList(newMeals: List<Meal>) {
        meals = newMeals
        notifyDataSetChanged()  // Ideally, you should use DiffUtil to improve performance.
    }
}
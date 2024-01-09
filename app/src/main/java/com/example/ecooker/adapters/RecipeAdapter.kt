package com.example.ecooker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecooker.R
import com.example.ecooker.databinding.ItemRecipeBinding
import com.example.ecooker.models.Recipe

class RecipeAdapter(
    private val recipeList: List<Recipe>,
    private val listener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: Recipe)
    }

    class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe, listener: OnRecipeClickListener) {
            with(binding) {
                recipeName.text = recipe.name
                recipeDifficulty.text = "${recipe.difficulty.toPolishString()}"
                difficultyIV.setImageResource(recipe.difficulty.setIcon())
                recipeSavedCount.text = "Polubienia : ${recipe.savedCount}"
                recipeTime.text = "${recipe.time} min"

                val correctedUrl = recipe.image.replace("localhost", "10.0.2.2")
                Glide.with(root.context)
                    .load(correctedUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(recipeImage)

                root.setOnClickListener { listener.onRecipeClick(recipe) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipeList[position], listener)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}

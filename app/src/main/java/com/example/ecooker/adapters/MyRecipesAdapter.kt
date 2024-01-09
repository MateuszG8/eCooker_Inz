package com.example.ecooker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecooker.databinding.ItemMyRecipeBinding
import com.example.ecooker.models.Recipe

class MyRecipeAdapter(
    private val recipeList: List<Recipe>,
    private val listener: OnMyRecipeClickListener
) : RecyclerView.Adapter<MyRecipeAdapter.MyRecipeViewHolder>() {

    interface OnMyRecipeClickListener {
        fun onRecipeClick(recipe: Recipe)
        fun onEditButtonClick(recipe: Recipe)
    }

    class MyRecipeViewHolder(private val binding: ItemMyRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe, listener: OnMyRecipeClickListener) {
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
                editButton.setOnClickListener { listener.onEditButtonClick(recipe) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecipeViewHolder {
        val binding = ItemMyRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyRecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyRecipeViewHolder, position: Int) {
        holder.bind(recipeList[position], listener)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}
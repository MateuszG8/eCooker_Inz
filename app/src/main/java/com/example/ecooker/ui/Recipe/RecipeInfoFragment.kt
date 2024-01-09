package com.example.ecooker.ui.Recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecooker.R
import com.example.ecooker.databinding.FragmentRecipeInfoBinding
import com.example.ecooker.models.Rate
import com.example.ecooker.models.RateRequest
import com.example.ecooker.models.SaveRecipeRequest
import com.example.ecooker.ui.dialogs.CommentsBottomSheetFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class RecipeInfoFragment : Fragment() {



    private lateinit var binding: FragmentRecipeInfoBinding
    private val viewModel: RecipeInfoViewModel by viewModels()
    private lateinit var isRecipeSavedObserver: Observer<Boolean>
    private lateinit var isRecipeRated: Observer<RateRequest>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Używamy RecipeInfoFragmentArgs do odczytania argumentu
        val args = RecipeInfoFragmentArgs.fromBundle(requireArguments())
        val recipe = args.recipe


        viewModel.isRecipeSaved(recipe.id)

        isRecipeSavedObserver = Observer {
            if (it){
                binding.followButton.visibility = View.GONE
                binding.unfollowButton.visibility = View.VISIBLE
            } else {
                binding.followButton.visibility = View.VISIBLE
                binding.unfollowButton.visibility = View.GONE
            }
        }
        viewModel.isRecipeSaved.observe(viewLifecycleOwner, isRecipeSavedObserver)

        binding.followButton.setOnClickListener {
            val postId = SaveRecipeRequest(recipeId = recipe.id)
            viewModel.saveRecipe(postId){
                Toast.makeText(context,"Zaloguj się, aby zapisać przepis", Toast.LENGTH_SHORT).show()
            }

        }
        binding.unfollowButton.setOnClickListener {
            viewModel.isRecipeSaved.postValue(false)
            viewModel.unSaveRecipe(recipe.id)
        }

        // Uzupełnianie widoku danymi z przepisu

        val correctedUrl = recipe.image.replace("localhost", "10.0.2.2")
        Glide.with(binding.recipeIV.context)
            .load(correctedUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .into(binding.recipeIV)

        recipe.tags.forEach { item ->
            val chip = Chip(context)
            chip.text = item
            chip.isCloseIconVisible = false
            chip.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("query", item)
                findNavController().navigate(R.id.action_global_to_searchFragment, bundle)
            }
            binding.chipGroup.addView(chip)
        }
        binding.recipeName.text = recipe.name
        binding.recipeDescription.text = recipe.description
        binding.difficultyIV.setImageResource(recipe.difficulty.setIcon())
        binding.recipeDifficulty.text = recipe.difficulty.toPolishString()
        if (recipe.calories != null) {
            binding.recipeCalories.text = recipe.calories.toString()
        } else {
            binding.recipeCalories.visibility = View.GONE
            binding.caloriesIcon.visibility = View.GONE
            binding.caloriesIconLabel.visibility = View.GONE
            binding.iconsLayout.weightSum = 3F
        }
        binding.recipePortions.text = recipe.portions.toString()
        binding.recipeTime.text = "${recipe.time} minut"
        binding.recipeIngredients.text = recipe.ingredients.joinToString("\n") { "${it.name} ${it.ammount} ${it.unit}" }
        binding.recipeInstructions.text = recipe.instructions.withIndex().joinToString("\n") { (index, instruction) ->
            if(index==0){
                "Krok ${index + 1}\n$instruction"
            } else
                "\nKrok ${index + 1}\n$instruction"
        }

        binding.commentsButton.setOnClickListener {
            val bottomSheetFragment = CommentsBottomSheetFragment()
            val bundle = Bundle()
            bundle.putString("recipeId", recipe.id)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }


        viewModel.getRating(recipe.id)
        val ratingBar = binding.ratingBar
        if(recipe.rating == null){
            binding.ratingTV.text = "Bądź pierwszą osobą, która oceni ten przepis"
        }else{
            ratingBar.rating = recipe.rating.toFloat()
            binding.ratingTV.text = "Ocena użytkowników: ${String.format("%.2f", recipe.rating.toFloat())}"
        }

        isRecipeRated = Observer { value ->
           if (value.userRate != 0F) {
                binding.userRatingTV.text = "Twoja ocena: ${value.userRate}"
                binding.ratingTV.text = "Ocena użytkowników: ${String.format("%.2f", value.rate)}"
                binding.userRatingTV.visibility = View.VISIBLE
                ratingBar.rating = value.userRate
            }

        }
        viewModel.rating.observe(viewLifecycleOwner, isRecipeRated)

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if(fromUser) {
                val value = Rate(ceil(rating.toDouble()).toInt())
                viewModel.rateRecipe(value, recipe.id){
                    Toast.makeText(context,"Zaloguj się aby ocenić", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
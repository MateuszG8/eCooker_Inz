package com.example.ecooker.ui.savedRecipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecooker.databinding.FragmentSavedRecipesBinding
import com.example.ecooker.models.Recipe
import com.example.ecooker.adapters.RecipeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedRecipesFragment : Fragment(), RecipeAdapter.OnRecipeClickListener {


    private val viewModel: SavedRecipesViewModel by viewModels()
    private lateinit var binding: FragmentSavedRecipesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedRecipesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.getSavedRecipes()

        binding.savedRecipesFragment.setOnRefreshListener {
            viewModel.getSavedRecipes()
            binding.savedRecipesFragment.isRefreshing = false
        }
        viewModel.recipes.observe(viewLifecycleOwner) { recipeList ->
            recyclerView.adapter = RecipeAdapter(recipeList, this)
        }
    }



    override fun onRecipeClick(recipe: Recipe) {
        val action = SavedRecipesFragmentDirections.actionSavedRecipesToRecipeInfo(recipe)
        findNavController().navigate(action)
    }

}
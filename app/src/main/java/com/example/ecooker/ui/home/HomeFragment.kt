package com.example.ecooker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.ecooker.RecipeViewModel
import com.example.ecooker.databinding.FragmentHomeBinding
import com.example.ecooker.models.Recipe
import com.example.ecooker.adapters.RecipeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), RecipeAdapter.OnRecipeClickListener {


    private val recipeModel: RecipeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipeModel.fetchAllRecipes()
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        binding.homeFragment.setOnRefreshListener {
            recipeModel.fetchAllRecipes()
            binding.homeFragment.isRefreshing = false
        }
        recipeModel.recipes.observe(viewLifecycleOwner) { recipeList ->
            recyclerView.adapter = RecipeAdapter(recipeList, this)
        }
    }

    override fun onRecipeClick(recipe: Recipe) {
        // Kod do przejścia do innego fragmentu, np. RecipeInfoFragment
        val action = HomeFragmentDirections.actionNavHomeToNavRecipeInfo(recipe)
        findNavController().navigate(action)
    }
}

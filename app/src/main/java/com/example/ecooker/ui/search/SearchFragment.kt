package com.example.ecooker.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecooker.RecipeViewModel
import com.example.ecooker.databinding.FragmentSearchBinding
import com.example.ecooker.models.Recipe
import com.example.ecooker.adapters.RecipeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), RecipeAdapter.OnRecipeClickListener {


    private val recipeModel: RecipeViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = arguments?.getString("query")
        val queryFridge = arguments?.getString("queryFridge")

        when {
            query != null -> {
                // Działania dla wartości 'query'
                (requireActivity() as AppCompatActivity).supportActionBar?.title = query
                recipeModel.searchRecipesByName(query)
            }
            queryFridge != null -> {
                // Działania dla wartości 'queryFridge'
                (requireActivity() as AppCompatActivity).supportActionBar?.title = queryFridge
                recipeModel.searchRecipesByIngredients(queryFridge)
            }
            else -> {
                Log.e("SearchFragment", "Brak wartości 'query' ani 'queryFridge'")
            }
        }
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        binding.searchFragment.setOnRefreshListener {
            binding.searchFragment.isRefreshing = false
        }


        recipeModel.searchedRecipes.observe(viewLifecycleOwner) { recipes ->

            recyclerView.adapter = RecipeAdapter(recipes, this)
        }
    }

    override fun onRecipeClick(recipe: Recipe) {
        val action =SearchFragmentDirections.actionNavSearchToNavRecipeInfo(recipe)
        findNavController().navigate(action)
    }
}
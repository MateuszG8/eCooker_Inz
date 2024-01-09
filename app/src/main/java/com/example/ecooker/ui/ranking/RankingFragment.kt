package com.example.ecooker.ui.ranking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecooker.RecipeViewModel
import com.example.ecooker.databinding.FragmentRankingBinding
import com.example.ecooker.models.Recipe
import com.example.ecooker.adapters.RecipeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingFragment : Fragment(), RecipeAdapter.OnRecipeClickListener {


    private val recipeModel: RecipeViewModel by viewModels()
    private lateinit var binding: FragmentRankingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRankingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        binding.rankingFragment.setOnRefreshListener {
            recipeModel.fetchAllRecipes()
            binding.rankingFragment.isRefreshing = false
        }
        recipeModel.recipes.observe(viewLifecycleOwner) { recipeList ->
            recyclerView.adapter = RecipeAdapter(recipeList, this)
        }
    }


    override fun onRecipeClick(recipe: Recipe) {
        val action = RankingFragmentDirections.actionNavRankingToNavRecipeInfo(recipe)
        findNavController().navigate(action)
    }

}
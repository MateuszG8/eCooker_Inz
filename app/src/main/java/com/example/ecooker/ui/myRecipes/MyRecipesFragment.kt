package com.example.ecooker.ui.myRecipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecooker.databinding.FragmentMyRecipesBinding
import com.example.ecooker.models.Recipe
import com.example.ecooker.adapters.MyRecipeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRecipesFragment : Fragment(), MyRecipeAdapter.OnMyRecipeClickListener {

    private val viewModel: MyRecipesViewModel by viewModels()
    private lateinit var binding: FragmentMyRecipesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRecipesBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.getMyRecipes()

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
               recyclerView.adapter = MyRecipeAdapter(recipes, this)
        }

    }

    override fun onRecipeClick(recipe: Recipe) {
        val action = MyRecipesFragmentDirections.actionMyRecipesHomeToNavRecipeInfo(recipe)
        findNavController().navigate(action)
    }

    override fun onEditButtonClick(recipe: Recipe) {
        val action = MyRecipesFragmentDirections.actionNavMyRecipesToNavEditRecipe(recipe)
        findNavController().navigate(action)
    }

}
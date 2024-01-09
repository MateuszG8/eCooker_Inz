package com.example.ecooker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecooker.models.Recipe
import com.example.ecooker.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _searchedRecipes  = MutableLiveData<List<Recipe>>()
    val searchedRecipes: LiveData<List<Recipe>> get() = _searchedRecipes

    init {
        fetchAllRecipes()
    }

    fun fetchAllRecipes() {
        repository.getAllRecipes(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    _recipes.value = response.body()
                    Log.d("Recipes", response.body().toString())
                } else {
                    Log.d("recipe", "Problem z zasobem")

                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Log.d("recipe", "Problem z serwerem")
            }
        })
    }

    fun searchRecipesByIngredients(ingredients: String) {
        repository.searchRecipesByIngredients(ingredients, object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    _searchedRecipes.value = response.body()
                    Log.d("RecipesByIngredients", response.body().toString())
                } else {
                    Log.d("recipeByIngredients", "Problem z zasobem")
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Log.d("recipeByIngredients", "Problem z serwerem")
            }
        })
    }

    fun searchRecipesByName(name: String) {
        repository.searchRecipesByName(name, object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    _searchedRecipes.value = response.body()
                    Log.d("RecipesByName", _searchedRecipes.value.toString())

                } else {
                    Log.d("recipeByName", "Problem z zasobem")
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Log.d("recipeByName", "Problem z serwerem")
            }
        })
    }
}






package com.example.ecooker.ui.savedRecipes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecooker.models.Recipe
import com.example.ecooker.repository.RecipeRepository
import com.example.ecooker.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SavedRecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    val token = "Bearer ${tokenRepository.getToken()}"

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes


    fun getSavedRecipes(){
        recipeRepository.getSavedRecipes(token, object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    _recipes.value = response.body()
                    Log.d("savedRecipes", recipes.value.toString())

                } else {
                    Log.d("savedRecipes", response.body().toString())
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Log.d("savedRecipesFailServer", t.toString())
            }

        })
    }



}
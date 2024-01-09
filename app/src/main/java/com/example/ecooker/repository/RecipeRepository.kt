package com.example.ecooker.repository

import com.example.ecooker.api.ApiClient
import com.example.ecooker.models.Recipe
import com.example.ecooker.models.AddRecipe
import com.example.ecooker.models.Comment
import com.example.ecooker.models.CommentRequest
import com.example.ecooker.models.EditRecipe
import com.example.ecooker.models.Rate
import com.example.ecooker.models.RateRequest
import com.example.ecooker.models.SaveRecipeRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Callback

class RecipeRepository {
    private val apiService = ApiClient.api

    fun getAllRecipes(callback: Callback<List<Recipe>>) {
        apiService.getAllRecipes().enqueue(callback)
    }
    fun getTags(token: String, callback: Callback<Map<String, List<String>>>) {
        apiService.getTags(token).enqueue(callback)
    }
    fun addRecipe(token: String, recipe: AddRecipe, callback: Callback<ResponseBody>) {
        apiService.addRecipe(token, recipe).enqueue(callback)
    }

    fun searchRecipesByIngredients(ingredients: String, callback: Callback<List<Recipe>>) {
        apiService.searchRecipesByIngredients(ingredients).enqueue(callback)
    }

    fun searchRecipesByName(name: String, callback: Callback<List<Recipe>>) {
        apiService.searchRecipesByName(name).enqueue(callback)
    }

    fun uploadRecipeImage(token: String, recipeId: String, image: MultipartBody.Part, callback: Callback<ResponseBody>) {
        apiService.uploadRecipeImage(token, recipeId, image).enqueue(callback)
    }

    fun isRecipeSaved(token: String, recipeId: String, callback: Callback<Boolean>) {
        apiService.isRecipeSaved(token,recipeId).enqueue(callback)
    }
    fun saveRecipe(token: String, recipeId: SaveRecipeRequest, callback: Callback<ResponseBody>) {
        apiService.saveRecipe(token, recipeId).enqueue(callback)
    }
    fun deleteRecipe(token: String, id: String, callback: Callback<ResponseBody>) {
        apiService.deleteRecipe(token, id).enqueue(callback)
    }

    fun getMyRecipes(token: String, callback: Callback<List<Recipe>>) {
        apiService.getMyRecipes(token).enqueue(callback)
    }

    fun getSavedRecipes(token: String, callback: Callback<List<Recipe>>) {
        apiService.getSavedRecipes(token).enqueue(callback)
    }

    fun updateRecipe(token: String, recipe: EditRecipe, callback: Callback<ResponseBody>) {
        apiService.updateRecipe(token, recipe).enqueue(callback)
    }

    fun deleteFromSaveRecipes(token: String, recipeId: String, callback: Callback<ResponseBody>) {
        apiService.unfollowRecipe(token, recipeId).enqueue(callback)
    }

    fun rateRecipe(token: String, rateRequest: Rate, id: String, callback: Callback<ResponseBody>) {
        apiService.rateRecipe(token,  rateRequest, id).enqueue(callback)
    }

    fun getRecipeRating(token: String, id: String,callback: Callback<RateRequest>) {
        apiService.getRecipeRating(token, id).enqueue(callback)
    }


}
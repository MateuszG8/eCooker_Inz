package com.example.ecooker.ui.editRecipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecooker.models.AddRecipe
import com.example.ecooker.models.EditRecipe
import com.example.ecooker.models.Ingredient
import com.example.ecooker.repository.RecipeRepository
import com.example.ecooker.repository.TokenRepository
import com.example.ecooker.repository.UserAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    val token = "Bearer ${tokenRepository.getToken()}"

    private val _tags = MutableLiveData<List<String>>()
    val tags: LiveData<List<String>> get() = _tags

    init {
        fetchTags()
    }



    fun fetchTags() {
        recipeRepository.getTags(token, object : Callback<Map<String, List<String>>> {
            override fun onResponse(call: Call<Map<String, List<String>>>, response: Response<Map<String, List<String>>>) {
                if (response.isSuccessful) {
                    val tagsMap = response.body()
                    val tagsList: List<String> = tagsMap?.flatMap { it.value } ?: listOf()
                    _tags.value = tagsList
                    Log.d("Tagi", tagsList.toString())
                } else {
                    Log.d("error tag", "Coś z serwerem jest nie tak")
                }
            }

            override fun onFailure(call: Call<Map<String, List<String>>>, t: Throwable) {
                Log.d("error tag", t.toString())
            }
        })
    }
    fun updateRecipe(recipe: EditRecipe,callback: () -> Unit) {
        recipeRepository.updateRecipe(token, recipe, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBodyString = response.body()?.string()?.replace("\"", "")
                    Log.d("response", responseBodyString ?: "Null body")
                    callback()
                } else {
                    Log.d("addRecipeError", response.toString())
                    Log.d("addRecipeErrorRecipe", recipe.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("addRecipeErrorRecipe", t.toString())
            }
        })
    }

    fun uploadPhoto(id: String, image: MultipartBody.Part){
        recipeRepository.uploadRecipeImage(token, id, image, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body()?.string() ?: "Brak wiadomości od serwera"
                    Log.d("Photo", serverMessage)
                } else {
                    Log.d("Photo", response.body().toString())
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Photo", t.toString())
            }
        })
    }



    fun deleteRecipe(id: String,  callback: () -> Unit){
        recipeRepository.deleteRecipe(token, id, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body()?.string() ?: "Brak wiadomości od serwera"
                    Log.d("delete", serverMessage)
                    callback()
                } else {
                    Log.d("deleteError", response.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("deleteServer", t.toString())
            }

        })
    }


}
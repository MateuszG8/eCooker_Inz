package com.example.ecooker.ui.Recipe

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecooker.models.Rate
import com.example.ecooker.models.RateRequest
import com.example.ecooker.models.SaveRecipeRequest
import com.example.ecooker.repository.RecipeRepository
import com.example.ecooker.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipeInfoViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    val token = "Bearer ${tokenRepository.getToken()}"
    val rating: MutableLiveData<RateRequest> = MutableLiveData()

    val isRecipeSaved: MutableLiveData<Boolean> = MutableLiveData()

    fun isRecipeSaved(id: String) {
        recipeRepository.isRecipeSaved(token, id, object : Callback<Boolean>{
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body() ?: false
                    Log.d("isSaved", serverMessage.toString())
                    isRecipeSaved.postValue(serverMessage)

                } else {
                    Log.d("isSaved", response.body().toString())

                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d("isSaved", t.toString())
            }
        })
    }

    fun saveRecipe(id: SaveRecipeRequest, callback:()->Unit) {
        recipeRepository.saveRecipe(token, id, object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body().toString() ?: "brak"
                    Log.d("SavedRecipeSuc", serverMessage)
                    isRecipeSaved.postValue(true)
                } else
                {
                    Log.d("SavedRecipeNotSuc", response.body().toString())
                    callback()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("SavedRecipeFail", t.toString())
            }
        })
    }
    fun unSaveRecipe(id: String) {
        recipeRepository.deleteFromSaveRecipes(token, id, object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body().toString() ?: "brak"
                    Log.d("DeleteRecipeSuc", serverMessage)
                    isRecipeSaved.postValue(false)
                } else
                {
                    Log.d("DeleteRecipeNotSuc", response.body().toString())
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("DeleteRecipeFail", t.toString())
            }
        })
    }

    fun rateRecipe(rateRequest: Rate, id: String, callback:()->Unit){
        recipeRepository.rateRecipe(token, rateRequest, id, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Rate", response.toString())
                    getRating(id)
                } else if (response.code() == 403){
                    Log.d("RateError403", response.toString())
                    callback()
                } else
                {
                    Log.d("RateError", response.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("RateServerError", t.toString())
            }

        })
    }

    fun getRating(id: String){
        recipeRepository.getRecipeRating(token, id, object : Callback<RateRequest>{
            override fun onResponse(call: Call<RateRequest>, response: Response<RateRequest>) {
                if (response.isSuccessful) {
                    Log.d("Rate", response.body().toString())
                    val ratingValue = response.body() ?: RateRequest(rate = -1F, userRate = 4F)
                    rating.postValue(ratingValue)
                } else {
                    Log.d("RateError", response.toString())
                }
            }

            override fun onFailure(call: Call<RateRequest>, t: Throwable) {
                Log.d("RateServerError", t.toString())
            }

        })
    }



}




package com.example.ecooker.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecooker.models.Tags
import com.example.ecooker.repository.RecipeRepository
import com.example.ecooker.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    // MutableLiveData dla mapy kategorii z tagami
    private val _tagCategories = MutableLiveData<Map<String, List<String>>>()
    val tagCategories: LiveData<Map<String, List<String>>> get() = _tagCategories

    init {
        fetchTagCategories()
    }

    private fun fetchTagCategories() {
        val token = "Bearer ${tokenRepository.getToken()}"
        recipeRepository.getTags(token, object : Callback<Map<String, List<String>>> {
            override fun onResponse(call: Call<Map<String, List<String>>>, response: Response<Map<String, List<String>>>) {
                if (response.isSuccessful) {
                    _tagCategories.value = response.body()
                    Log.d("Categories", _tagCategories.value.toString())
                } else {
                    Log.d("error categories", "Coś z serwerem jest nie tak")
                }
            }

            override fun onFailure(call: Call<Map<String, List<String>>>, t: Throwable) {
                Log.d("error categories", t.toString())
            }
        })
    }
    fun convertToTagsList(categoriesMap: Map<String, List<String>>): List<Tags> {
        return categoriesMap.map { Tags(it.key, it.value) }
    }
}
package com.example.ecooker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecooker.models.Comment
import com.example.ecooker.models.CommentRequest
import com.example.ecooker.repository.CommentRepository
import com.example.ecooker.repository.RecipeRepository
import com.example.ecooker.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val tokenRepository: TokenRepository
): ViewModel() {

    val token = "Bearer ${tokenRepository.getToken()}"
    private val _comments  = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments


    fun getComments(id: String){
        commentRepository.getComments(id, object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    _comments.value = response.body()
                    Log.d("comments",response.body().toString())
                } else{
                    Log.d("commentsEror",response.body().toString())
                }
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Log.d("commentsErorServer",t.toString())
            }

        })
    }

    fun addComment(commentRequest: CommentRequest, recipeId: String){
        commentRepository.addComment(token, commentRequest, recipeId, object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("comments",response.body().toString())
                    getComments(recipeId)
                } else{
                    Log.d("commentsErorr",response.toString())
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("commentsErorServer",t.toString())
            }

        })
    }

    fun updateComment(commentRequest: CommentRequest, recipeId: String, commentId: String,  callback: () -> Unit) {
        commentRepository.updateComment(token, commentRequest, recipeId, commentId, object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d("comments",response.body().toString())
                        callback()
                    } else{
                        Log.d("commentsErorr",response.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("commentsErorServer",t.toString())
                }

            })
    }
    fun deleteComment(recipeId: String, commentId: String){
        commentRepository.deleteComment(token,recipeId,commentId, object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("comments",response.body().toString())
                } else{
                    Log.d("commentsEror",response.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("commentsErorServer",t.toString())
            }

        })

    }




}
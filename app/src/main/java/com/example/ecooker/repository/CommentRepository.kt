package com.example.ecooker.repository

import com.example.ecooker.api.ApiClient
import com.example.ecooker.models.Comment
import com.example.ecooker.models.CommentRequest
import okhttp3.ResponseBody
import retrofit2.Callback

class CommentRepository {
    private val apiService = ApiClient.api

    fun getComments(id: String, callback: Callback<List<Comment>>) {
        apiService.getCommentsToCurrentRecipe(id).enqueue(callback)
    }

    fun addComment(token: String, commentRequest: CommentRequest, recipeId: String, callback: Callback<ResponseBody>){
        apiService.addCommentsToRecipe(token,commentRequest,recipeId).enqueue(callback)
    }

    fun updateComment(token: String, commentRequest: CommentRequest, recipeId: String, commentId: String, callback: Callback<ResponseBody>){
        apiService.updateComment(token,commentRequest,recipeId,commentId).enqueue(callback)
    }

    fun deleteComment(token: String, recipeId: String, commentId: String, callback: Callback<ResponseBody>){
        apiService.deleteComment(token,recipeId,commentId).enqueue(callback)
    }
}
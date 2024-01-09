package com.example.ecooker.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ecooker.api.ApiClient
import com.example.ecooker.models.LoginResponse
import retrofit2.Callback

class TokenRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    private val apiService = ApiClient.api
    fun getToken(): String? = sharedPreferences.getString("jwt_token", null)

    fun setToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
    }

    fun clearToken() {
        sharedPreferences.edit().remove("jwt_token").apply()
    }

    fun getRefreshToken(): String? = sharedPreferences.getString("jwt_token_refresh", null)

    fun setRefreshToken(token: String) {
        sharedPreferences.edit().putString("jwt_token_refresh", token).apply()
    }

    fun clearRefreshToken() {
        sharedPreferences.edit().remove("jwt_token_refresh").apply()
    }
    fun refreshToken(refreshToken: String, callback: Callback<LoginResponse>) {
        apiService.refreshToken(refreshToken).enqueue(callback)
    }

}
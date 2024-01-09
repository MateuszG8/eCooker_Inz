package com.example.ecooker.repository

import com.example.ecooker.api.ApiClient
import com.example.ecooker.models.EditUser
import com.example.ecooker.models.LoginInfo
import com.example.ecooker.models.LoginResponse
import com.example.ecooker.models.Register
import com.example.ecooker.models.UserInfo
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Callback

class UserAuthRepository {

    private val apiService = ApiClient.api

    fun login(loginInfo: LoginInfo, callback: Callback<ResponseBody>) {
        apiService.login(loginInfo).enqueue(callback)
    }

    fun register(userInfo: Register, callback: Callback<ResponseBody>) {
        apiService.register(userInfo).enqueue(callback)
    }

    fun getUserInfo(token: String, callback: Callback<UserInfo>) {
        apiService.getUserInfo(token).enqueue(callback)
    }
    fun uploadImageProfile(token: String, image: MultipartBody.Part, callback: Callback<ResponseBody>) {
        apiService.uploadImageProfile(token, image).enqueue(callback)
    }

    fun editProfile(token: String,editUser: EditUser, callback: Callback<ResponseBody>) {
        apiService.editUser(token,editUser).enqueue(callback)
    }
}
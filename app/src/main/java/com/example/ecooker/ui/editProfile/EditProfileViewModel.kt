package com.example.ecooker.ui.editProfile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecooker.models.EditUser
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
class EditProfileViewModel  @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    val postResult: MutableLiveData<Int> = MutableLiveData()
    val token = "Bearer ${tokenRepository.getToken()}"

    fun uploadPhoto(image: MultipartBody.Part) {
        userAuthRepository.uploadImageProfile(token, image, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body()?.string() ?: "Brak wiadomości od serwera"
                    Log.d("Photo", serverMessage)
                    postResult.value=1
                } else {
                    Log.d("Photo", response.body().toString())
                    postResult.value=2
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Photo", t.toString())
                postResult.value=3
            }
        })
    }
    fun resetLiveData(){
        postResult.value=0
    }

    fun editProfile(editUser: EditUser) {
        userAuthRepository.editProfile(token, editUser, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body()?.string() ?: "Brak wiadomości od serwera"
                    Log.d("editUser", serverMessage)
                    postResult.value=1
                } else {
                    Log.d("editUser", response.body().toString())
                    postResult.value=2
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("editUserServerError", t.toString())
                postResult.value=3

            }
        })
    }

}
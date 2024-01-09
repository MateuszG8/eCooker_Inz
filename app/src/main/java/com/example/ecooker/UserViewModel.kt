package com.example.ecooker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.ecooker.models.LoginInfo
import com.example.ecooker.models.LoginResponse
import com.example.ecooker.models.Register
import com.example.ecooker.models.UserInfo
import com.example.ecooker.repository.TokenRepository
import com.example.ecooker.repository.UserAuthRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val authRepository: UserAuthRepository,
    private val tokenRepository: TokenRepository
) : AndroidViewModel(application) {

    val loginResult: MutableLiveData<Pair<Int, String>> = MutableLiveData()
    val registerResult: MutableLiveData<Pair<Int, String>> = MutableLiveData()
    val userDataResult: MutableLiveData<Boolean> = MutableLiveData()
    private val userDataObserver = Observer<Boolean> { success ->
        _isLoggedIn.value = success
    }

    private val _userData = MutableLiveData<UserInfo?>()
    val userData: LiveData<UserInfo?>
        get() = _userData

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    init {
        getUserInfo()
        userDataResult.observeForever(userDataObserver)

    }
    fun defaultResult(){
        registerResult.postValue(Pair(4,""))
    }
    fun logout() {
        tokenRepository.clearToken()
        tokenRepository.clearRefreshToken()
        _isLoggedIn.value = false
        loginResult.postValue(Pair(4, ""))
        _userData.value = null
    }

    fun login(userName: String, password: String) {
        val loginInfo = LoginInfo(userName, password)

        authRepository.login(loginInfo, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val serverMessage = response.body()?.string() ?: "Brak wiadomości od serwera"
                    val loginResponse = gson.fromJson(serverMessage, LoginResponse::class.java)
                    tokenRepository.setToken(loginResponse.token)
                    tokenRepository.setRefreshToken(loginResponse.refresh)
                    _isLoggedIn.value = true
                    getUserInfo()
                    loginResult.postValue(Pair(1, serverMessage))
                } else {
                    val serverErrorMessage = response.errorBody()?.string() ?: "Brak wiadomości od serwera"
                    loginResult.postValue(Pair(2, serverErrorMessage))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                loginResult.postValue(Pair(3, t.toString()))
            }
        })
    }

    fun register(firstName: String, lastName: String, userName: String, email: String, password: String) {
        val register = Register(firstName,lastName,userName,email, password)
        authRepository.register(register, object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val serverMessage = response.body()?.string() ?: "Brak wiadomości od serwera"
                    registerResult.postValue(Pair(1, serverMessage))
                } else {
                    val serverErrorMessage = response.errorBody()?.string() ?: "Brak wiadomości od serwera"
                    registerResult.postValue(Pair(2, serverErrorMessage))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                registerResult.postValue(Pair(3, t.toString()))
            }
        })
    }

    fun getUserInfo() {
        val token = "Bearer ${tokenRepository.getToken()}"
        if (token != null) {
            authRepository.getUserInfo(token, object : Callback<UserInfo> {
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    if (response.isSuccessful) {
                        _userData.value = response.body()
                        Log.d("user", _userData.value.toString())
                        userDataResult.postValue(true)
                    } else if (response.code() == 401) {
                        refreshToken()
                    } else {
                        Log.d("fail", response.errorBody()?.string() ?: "Brak wiadomości od serwera")
                        userDataResult.postValue(false)
                    }
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("error", t.toString())
                    userDataResult.postValue(false)
                }
            })
        }
    }
    fun refreshToken() {
        val refreshToken = tokenRepository.getRefreshToken() // zakładam, że masz taką funkcję
        if (refreshToken != null) {
            tokenRepository.refreshToken(refreshToken, object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            tokenRepository.setToken(it.token) // Ustawia nowy token
                        }
                        getUserInfo() // Po odświeżeniu tokena, pobierasz informacje o użytkowniku
                    } else {
                        logout()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    logout()
                }
            })
        }
    }
    override fun onCleared() {
        super.onCleared()
        userDataResult.removeObserver(userDataObserver)
    }
}

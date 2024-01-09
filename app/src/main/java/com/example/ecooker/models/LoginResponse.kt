package com.example.ecooker.models


data class LoginResponse(
    val token: String,
    val refresh: String,
    val message: String
)

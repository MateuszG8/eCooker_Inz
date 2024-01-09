package com.example.ecooker.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
@Parcelize
data class UserInfo(
        @SerializedName("_id")
        val userId: String,
        val firstName: String,
        val lastName: String,
        val userName: String,
        val email: String,
        val image: String
) : Parcelable

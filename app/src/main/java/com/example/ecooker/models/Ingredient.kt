package com.example.ecooker.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val name: String,
    val ammount: Int,
    val unit: String
) : Parcelable

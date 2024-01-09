package com.example.ecooker.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class AddRecipe(
    val name: String,
    val description: String?,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val tags: List<String>,
    val difficulty: Difficulty,
    val calories: Int?,
    val portions: Int,
    val time: Int
) : Parcelable

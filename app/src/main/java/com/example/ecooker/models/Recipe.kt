package com.example.ecooker.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Recipe(
    @SerializedName("recipe_id")
    val id: String,
    @SerializedName("author")
    val authorId: String,
    val name: String,
    val tags: List<String>,
    val image: String,
    val ingredients: List<Ingredient>,
    val description: String?,
    val instructions: List<String>,
    val difficulty: Difficulty,
    val calories: Int?,
    val portions: Int,
    @SerializedName("saved_count")
    val savedCount: Int,
    val rating: Double?,
    val time: Int
) : Parcelable
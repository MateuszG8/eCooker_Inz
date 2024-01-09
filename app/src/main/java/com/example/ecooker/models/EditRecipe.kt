package com.example.ecooker.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditRecipe(
    val id: String,
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

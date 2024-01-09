package com.example.ecooker.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val commentId: String,
    val comment: String,
    val user: String,
    val userId: String
): Parcelable

package com.example.ecooker.models

import java.io.Serializable

data class Tags(
    val categoryName: String,
    val tags: List<String>
) : Serializable

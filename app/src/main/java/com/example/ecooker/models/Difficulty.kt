package com.example.ecooker.models

import android.graphics.drawable.Icon
import com.example.ecooker.R

enum class Difficulty {
    easy, medium, hard;

    fun toPolishString(): String {
        return when (this) {
            easy -> "Łatwy"
            medium -> "Średni"
            hard -> "Trudny"
        }
    }

    fun setIcon(): Int {
        return when (this) {
            easy -> R.drawable.difficulty_easy
            medium -> R.drawable.difficulty_medium
            hard -> R.drawable.difficulty_hard
        }
    }
    
    companion object {
        fun toPolishStringList(): List<String> {
            return values().map { it.toPolishString() }
        }
    }
}
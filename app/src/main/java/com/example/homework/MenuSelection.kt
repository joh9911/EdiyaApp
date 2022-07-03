package com.example.homework

data class MenuSelection(
        val menuImageSource: String,
        val menuName: String,
        val menuPrice: String,
        var amount: String?,
        var size: String?
)
package com.app.spacenow.data.model

data class Space(
    val id: String,
    val name: String,
    val description: String,
    val capacity: Int,
    val available: Boolean,
    val imageResource: Int,
    val imageUri: String? = null
)
package com.app.spacenow.data.model

data class Space(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val capacity: Int = 0,
    val available: Boolean = true,
    val imageResource: Int = 0
)
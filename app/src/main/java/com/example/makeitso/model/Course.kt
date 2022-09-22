package com.example.makeitso.model

data class Course(
    val id: String = "",
    val title: String = "",
    val priority: String = "",
    val dueDate: String = "",
    val dueTime: String = "",
    val description: String = "",
    val url: String = "",
    val flag: Boolean = false,
    val completed: Boolean = false,
    val userId: String = ""

)

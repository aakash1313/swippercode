package com.example.swipper.database.user

data class User (
    val uid: String? = "",
    val name: String? = "",
    val email: String? = "",
    val gender: String? = "",
    var age: String?="",
    val preferredGender: String? = "",
    val imageUrl: String? = ""
)
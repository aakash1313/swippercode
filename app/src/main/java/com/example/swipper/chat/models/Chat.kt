package com.example.swipper.chat.models

data class Chat(
    val userId: String?= "",
    val chatId: String?= "",
    val otherUserId: String?= "",
    val name: String?= "",
    val imageUrl: String?= ""
)
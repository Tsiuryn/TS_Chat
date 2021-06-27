package com.ts.alex.ts_chat

data class Message(
    val name: String,
    val text: String? = null,
    val imageUrl: String? = null
)

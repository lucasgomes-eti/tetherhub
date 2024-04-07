package model

data class Message(
    val id: String,
    val sender: User,
    val content: String,
)

package model

open class Message(
    val id: String,
    val sender: User,
    val content: String,
)

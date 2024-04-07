package model

open class Conversation(
    val id: String,
    val users: List<User>,
    val lastMessage: Message
)
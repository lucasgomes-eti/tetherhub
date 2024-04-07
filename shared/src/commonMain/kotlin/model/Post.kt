package model

open class Post(
    val id: String,
    val author: User,
    val content: String,
    val likes: Int
)

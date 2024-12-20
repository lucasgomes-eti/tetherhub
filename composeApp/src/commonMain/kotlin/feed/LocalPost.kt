package feed

data class LocalPost(private val post: Post, val isLiked: Boolean) :
    Post(post.id, post.author, post.content, post.likes)
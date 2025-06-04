package noul.oe.core.post.domain

data class PostLike(
    val id: Long? = null,
    val userId: String,
    val postId: Long,
)
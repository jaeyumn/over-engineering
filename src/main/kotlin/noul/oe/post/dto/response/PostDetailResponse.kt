package noul.oe.post.dto.response

import noul.oe.post.entity.Post
import noul.oe.user.entity.User
import java.time.LocalDateTime

data class PostDetailResponse(
    val postId: Long,
    val userId: String,
    val username: String,
    val editable: Boolean,
    val title: String,
    val content: String,
    val viewCount: Long,
    var likeCount: Long,
    val commentCount: Long,
    val liked: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(post: Post, user: User, likeCount: Long, commentCount: Long, liked: Boolean): PostDetailResponse = PostDetailResponse(
            postId = post.id,
            userId = post.userId,
            username = user.username,
            editable = true, // TODO
            title = post.title,
            content = post.content,
            viewCount = post.viewCount,
            likeCount = likeCount,
            commentCount = commentCount,
            liked = liked,
            createdAt = post.createdAt,
        )
    }
}
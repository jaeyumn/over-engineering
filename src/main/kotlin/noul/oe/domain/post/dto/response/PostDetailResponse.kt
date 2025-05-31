package noul.oe.domain.post.dto.response

import noul.oe.domain.post.entity.Post
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
    val commentCount: Int,
    val liked: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(post: Post, username: String, likeCount: Long, commentCount: Int, liked: Boolean): PostDetailResponse = PostDetailResponse(
            postId = post.id,
            userId = post.userId,
            username = username,
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
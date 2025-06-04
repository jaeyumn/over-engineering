package noul.oe.core.post.application.port.input

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
)
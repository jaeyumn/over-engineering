package noul.oe.core.post.application.port.input

import java.time.LocalDateTime

data class PostPageResponse(
    val postId: Long,
    val title: String,
    val content: String,
    val username: String,
    val createdAt: LocalDateTime,
    val likeCount: Long,
    val commentCount: Int,
    val viewCount: Long,
)
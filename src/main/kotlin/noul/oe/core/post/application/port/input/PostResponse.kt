package noul.oe.core.post.application.port.input

import noul.oe.support.port.dto.CommentInfo
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

data class PostDetailWithCommentsResponse(
    val post: PostDetailResponse,
    val comments: List<CommentInfo>
)

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
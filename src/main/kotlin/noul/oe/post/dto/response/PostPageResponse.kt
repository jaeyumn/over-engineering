package noul.oe.post.dto.response

import noul.oe.post.entity.Post
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
) {
    companion object {
        fun from(post: Post, username: String, commentCount: Int): PostPageResponse = PostPageResponse(
            postId = post.id,
            title = post.title,
            content = post.content,
            username = username,
            createdAt = post.createdAt,
            likeCount = post.likeCount,
            commentCount = commentCount,
            viewCount = post.viewCount,
        )
    }
}
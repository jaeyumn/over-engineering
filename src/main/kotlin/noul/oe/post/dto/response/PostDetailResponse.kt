package noul.oe.post.dto.response

import noul.oe.post.entity.Post

data class PostDetailResponse(
    val postId: Long,
    val userId: String,
    val title: String,
    val content: String,
    val viewCount: Long,
    var likeCount: Long,
) {
    companion object {
        fun from(post: Post): PostDetailResponse = PostDetailResponse(
            postId = post.id,
            userId = post.userId,
            title = post.title,
            content = post.content,
            viewCount = post.viewCount,
            likeCount = post.likeCount,
        )
    }
}
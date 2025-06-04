package noul.oe.core.post.application.port.input

import noul.oe.support.port.dto.CommentInfo

data class PostDetailWithCommentsResponse(
    val post: PostDetailResponse,
    val comments: List<CommentInfo>
)

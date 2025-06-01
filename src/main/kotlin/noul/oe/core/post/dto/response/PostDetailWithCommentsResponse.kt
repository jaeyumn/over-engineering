package noul.oe.core.post.dto.response

import noul.oe.support.port.dto.CommentInfo

data class PostDetailWithCommentsResponse(
    val post: PostDetailResponse,
    val comments: List<CommentInfo>
)

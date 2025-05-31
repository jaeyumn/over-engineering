package noul.oe.domain.post.dto.response

import noul.oe.support.info.dto.CommentInfo

data class PostDetailWithCommentsResponse(
    val post: PostDetailResponse,
    val comments: List<CommentInfo>
)

package noul.oe.core.post.application.port.input

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostQueryPort {
    fun readDetail(postId: Long): PostDetailResponse
    fun readAll(pageable: Pageable) : Page<PostPageResponse>
    fun readWithComments(postId: Long): PostDetailWithCommentsResponse
}
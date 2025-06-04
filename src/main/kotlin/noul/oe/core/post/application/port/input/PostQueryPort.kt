package noul.oe.core.post.application.port.input

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostQueryPort {
    fun readDetail(condition: ReadDetailCondition): PostDetailResponse
    fun readAll(pageable: Pageable) : Page<PostPageResponse>
    fun readWithComments(condition: ReadWithCommentsCondition): PostDetailWithCommentsResponse
}
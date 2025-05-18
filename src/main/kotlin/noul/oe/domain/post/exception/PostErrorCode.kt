package noul.oe.domain.post.exception

import noul.oe.support.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class PostErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    POST_NOT_FOUND("POST_NOT_FOUND", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_LIKED_POST("ALREADY_LIKED_POST", "이미 좋아요를 누른 게시글입니다.", HttpStatus.CONFLICT),
    PERMISSION_DENIED("PERMISSION_DENIED", "게시글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
}
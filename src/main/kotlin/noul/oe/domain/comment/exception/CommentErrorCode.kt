package noul.oe.domain.comment.exception

import noul.oe.support.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class CommentErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_COMMENT_ACCESS("UNAUTHORIZED_COMMENT_ACCESS", "댓글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
}
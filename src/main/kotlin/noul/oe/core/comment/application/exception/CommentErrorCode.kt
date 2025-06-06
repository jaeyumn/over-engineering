package noul.oe.core.comment.application.exception

import noul.oe.support.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class CommentErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    COMMENT_NOT_FOUND(NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    UNAUTHORIZED_COMMENT_ACCESS(FORBIDDEN, "UNAUTHORIZED_COMMENT_ACCESS", "댓글에 대한 권한이 없습니다."),
}
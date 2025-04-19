package noul.oe.comment.exception

import noul.oe.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class CommentErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "Comment not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_COMMENT_ACCESS("UNAUTHORIZED_COMMENT_ACCESS", "Unauthorized access to comment", HttpStatus.FORBIDDEN),
}
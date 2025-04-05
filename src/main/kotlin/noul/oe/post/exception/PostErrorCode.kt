package noul.oe.post.exception

import noul.oe.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class PostErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    POST_NOT_FOUND("POST_NOT_FOUND", "Post not found", HttpStatus.NOT_FOUND),
    ALREADY_LIKED_POST("ALREADY_LIKED_POST", "Already liked post", HttpStatus.CONFLICT),
    PERMISSION_DENIED("PERMISSION_DENIED", "Permission denied for post", HttpStatus.FORBIDDEN),
}
package noul.oe.core.post.application.exception

import noul.oe.support.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class PostErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String,
) : ErrorCode {
    POST_NOT_FOUND(NOT_FOUND, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다."),
    POST_LIKE_NOT_FOUND(NOT_FOUND, "POST_LIKE_NOT_FOUND", "게시글 좋아요 정보를 찾을 수 없습니다."),
    ALREADY_LIKED_POST(CONFLICT, "ALREADY_LIKED_POST", "이미 좋아요를 누른 게시글입니다."),
    PERMISSION_DENIED(FORBIDDEN, "PERMISSION_DENIED", "게시글에 대한 권한이 없습니다."),
}
package noul.oe.core.comment.application.exception

import noul.oe.support.exception.DomainException

class CommentNotFoundException(commentId: Long) :
    DomainException(CommentErrorCode.COMMENT_NOT_FOUND, commentId.toString())

class UnauthorizedCommentAccessException(userId: String) :
    DomainException(CommentErrorCode.UNAUTHORIZED_COMMENT_ACCESS, userId)

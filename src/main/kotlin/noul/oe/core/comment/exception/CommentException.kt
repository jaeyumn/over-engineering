package noul.oe.core.comment.exception

import noul.oe.support.exception.BaseException

class CommentNotFoundException(logMessage: String) : BaseException(CommentErrorCode.COMMENT_NOT_FOUND, logMessage)
class UnauthorizedCommentAccessException(logMessage: String) : BaseException(CommentErrorCode.UNAUTHORIZED_COMMENT_ACCESS, logMessage)

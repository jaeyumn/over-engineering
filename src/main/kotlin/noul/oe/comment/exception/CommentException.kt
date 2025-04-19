package noul.oe.comment.exception

import noul.oe.common.exception.BaseException

class CommentNotFoundException : BaseException(CommentErrorCode.COMMENT_NOT_FOUND)
class UnauthorizedCommentAccessException : BaseException(CommentErrorCode.UNAUTHORIZED_COMMENT_ACCESS)

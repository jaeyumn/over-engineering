package noul.oe.domain.post.exception

import noul.oe.support.exception.BaseException
import noul.oe.domain.post.exception.PostErrorCode.*

class PostNotFoundException(logMessage: String): BaseException(POST_NOT_FOUND, logMessage)
class AlreadyLikedPostException(logMessage: String): BaseException(ALREADY_LIKED_POST, logMessage)
class PostPermissionDeniedException(logMessage: String): BaseException(PERMISSION_DENIED, logMessage)
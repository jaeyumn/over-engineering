package noul.oe.post.exception

import noul.oe.support.exception.BaseException
import noul.oe.post.exception.PostErrorCode.*

class PostNotFoundException: BaseException(POST_NOT_FOUND)
class AlreadyLikedPostException: BaseException(ALREADY_LIKED_POST)
class PostPermissionDeniedException: BaseException(PERMISSION_DENIED)
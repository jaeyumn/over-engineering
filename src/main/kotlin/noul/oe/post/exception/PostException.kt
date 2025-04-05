package noul.oe.post.exception

import noul.oe.common.exception.BaseException
import noul.oe.post.exception.PostErrorCode.*

class PostNotFoundException: BaseException(POST_NOT_FOUND)
class AlreadyLikedPostException: BaseException(ALREADY_LIKED_POST)
class PostPermissionDeniedException: BaseException(PERMISSION_DENIED)
package noul.oe.core.post.application.exception

import noul.oe.core.post.application.exception.PostErrorCode.*
import noul.oe.support.exception.DomainException

class PostNotFoundException(postId: Long) : DomainException(POST_NOT_FOUND, postId.toString())
class PostLikeNotFoundException(postId: Long) : DomainException(POST_LIKE_NOT_FOUND, postId.toString())
class AlreadyLikedPostException(userId: String) : DomainException(ALREADY_LIKED_POST, userId)
class PostPermissionDeniedException(userId: String) : DomainException(PERMISSION_DENIED, userId)
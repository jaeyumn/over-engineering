package noul.oe.core.post.adapter.out

import noul.oe.core.post.domain.PostLike

object PostLikeMapper {
    fun toEntity(postLike: PostLike): PostLikeJpaEntity =
        PostLikeJpaEntity(
            id = postLike.id!!,
            postId = postLike.postId,
            userId = postLike.userId,
        )

    fun toDomain(entity: PostLikeJpaEntity): PostLike =
        PostLike(
            id = entity.id,
            postId = entity.postId,
            userId = entity.userId,
        )
}
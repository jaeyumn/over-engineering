package noul.oe.core.post.adapter.out

import noul.oe.core.post.domain.Post

object PostMapper {
    fun toEntity(post: Post): PostJpaEntity =
        PostJpaEntity(
            id = post.id!!,
            title = post.title,
            content = post.content,
            viewCount = 0,
            likeCount = 0,
            userId = post.userId
        )

    fun toDomain(entity: PostJpaEntity): Post =
        Post(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            content = entity.content,
            viewCount = entity.viewCount,
            likeCount = entity.likeCount,
            createdAt = entity.createdAt,
        )
}
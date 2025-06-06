package noul.oe.core.comment.adapter.out.persistence

import noul.oe.core.comment.domain.Comment

object CommentMapper {
    fun toEntity(comment: Comment): CommentJpaEntity =
        CommentJpaEntity(
            id = comment.id,
            content = comment.content,
            postId = comment.postId,
            userId = comment.userId,
            parentId = comment.parentId,
            createdAt = comment.createdAt,
            modifiedAt = comment.modifiedAt,
        )

    fun toDomain(entity: CommentJpaEntity): Comment =
        Comment(
            id = entity.id,
            content = entity.content,
            postId = entity.postId,
            userId = entity.userId,
            parentId = entity.parentId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
}
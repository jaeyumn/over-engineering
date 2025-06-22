package noul.oe.core.comment.adapter.out.info

import noul.oe.core.comment.adapter.out.persistence.CommentJpaRepository
import noul.oe.support.port.CommentInfoProvider
import noul.oe.support.port.dto.CommentInfo
import noul.oe.support.redis.CacheKey
import noul.oe.support.redis.RedisCacheService
import org.springframework.stereotype.Component

@Component
class CommentInfoProviderImpl(
    private val commentRepository: CommentJpaRepository,
    private val redisCacheService: RedisCacheService,
) : CommentInfoProvider {

    override fun getCommentCount(postId: Long): Int {
        val cacheKey = CacheKey.COMMENT_COUNT.with(postId)
        val lockKey = CacheKey.COMMENT_COUNT.lockFor(postId)
        val ttlMinutes = 60L

        val count = redisCacheService.getOrSetWithLock(
            key = cacheKey,
            lockKey = lockKey,
            ttlMinutes = ttlMinutes,
            valueFetcher = {
                commentRepository.countByPostId(postId)
            }
        )

        return count ?: 0
    }

    override fun getCommentList(postId: Long, userId: String): List<CommentInfo> {
        val commentList = commentRepository.findAllByPostId(postId)
        val commentInfoMap = commentList.associate { comment ->
            comment.id to CommentInfo(
                id = comment.id,
                content = comment.content,
                userId = comment.userId,
                username = "",
                createdAt = comment.createdAt.toString(),
                editable = comment.userId == userId,
            )
        }

        val rootCommentList = mutableListOf<CommentInfo>()
        commentList.forEach { comment ->
            val commentInfo = commentInfoMap[comment.id]!!
            if (comment.parentId == null) {
                rootCommentList.add(commentInfo)
            } else {
                commentInfoMap[comment.parentId]?.children?.add(commentInfo)
            }
        }

        return rootCommentList
    }

    override fun deleteAllComment(postId: Long) {
        commentRepository.deleteAllByPostId(postId)
    }
}
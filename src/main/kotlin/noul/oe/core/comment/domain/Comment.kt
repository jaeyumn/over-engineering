package noul.oe.core.comment.domain

import java.time.LocalDateTime

data class Comment(
    val id: Long = 0L,
    var content: String,
    val postId: Long,
    val userId: String,
    val parentId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val modifiedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun modify(newContent: String) {
        this.content = newContent
    }

    fun isNotOwnedBy(userId: String): Boolean = this.userId != userId
}
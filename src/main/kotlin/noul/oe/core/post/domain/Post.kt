package noul.oe.core.post.domain

import java.time.LocalDateTime

data class Post(
    val id: Long? = null,
    val userId: String,
    val title: String,
    val content: String,
    var viewCount: Long = 0,
    var likeCount: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun modify(title: String, content: String): Post {
        return this.copy(title = title, content = content)
    }

    fun like() {
        this.likeCount += 1
    }

    fun unlike() {
        if (this.likeCount == 0L)
            return
        this.likeCount -= 1
    }

    fun increaseViewCount() {
        this.viewCount += 1
    }
}
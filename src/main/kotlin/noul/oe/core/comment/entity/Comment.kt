package noul.oe.core.comment.entity

import jakarta.persistence.*
import noul.oe.support.entity.BaseEntity

@Entity
@Table(name = "comment", catalog = "comment_db")
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(nullable = false)
    val postId: Long,

    @Column(nullable = false)
    val userId: String,

    val parentId: Long? = null,
) : BaseEntity() {

    fun modify(newContent: String) {
        this.content = newContent
    }

    fun isNotOwnedBy(userId: String): Boolean = this.userId != userId
}
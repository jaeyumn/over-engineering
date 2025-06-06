package noul.oe.core.comment.adapter.out.persistence

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "comment", catalog = "comment_db")
class CommentJpaEntity(
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(nullable = false)
    val modifiedAt: LocalDateTime = LocalDateTime.now(),
)
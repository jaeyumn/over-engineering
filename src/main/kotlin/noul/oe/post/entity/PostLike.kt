package noul.oe.post.entity

import jakarta.persistence.*
import noul.oe.common.entity.BaseEntity

@Entity
class PostLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    val postId: Long,
) : BaseEntity()
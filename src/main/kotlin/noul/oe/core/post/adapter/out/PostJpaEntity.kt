package noul.oe.core.post.adapter.out

import jakarta.persistence.*
import noul.oe.support.entity.BaseEntity

@Entity
@Table(name = "post", catalog = "post_db")
class PostJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(nullable = false)
    var viewCount: Long = 0,

    @Column(nullable = false)
    var likeCount: Long = 0,

    @Column(nullable = false)
    var userId: String,
) : BaseEntity() {

    fun modify(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun increaseViewCount() {
        viewCount++
    }

    fun increaseLikeCount() {
        likeCount++
    }

    fun decreaseLikeCount() {
        likeCount--
    }
}
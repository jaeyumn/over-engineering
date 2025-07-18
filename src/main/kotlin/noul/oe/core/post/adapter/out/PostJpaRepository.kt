package noul.oe.core.post.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface PostJpaRepository : JpaRepository<PostJpaEntity, Long> {

    @Modifying
    @Query("""
        UPDATE PostJpaEntity  p
        SET     p.viewCount = p.viewCount + 1
        WHERE   p.id = :postId
    """)
    fun increaseViewCount(postId: Long)

    @Modifying
    @Query("""
        UPDATE  PostJpaEntity p
        SET     p.likeCount = p.likeCount + 1
        WHERE   p.id = :postId
    """)
    fun increaseLikeCount(postId: Long)

    @Modifying
    @Query("""
        UPDATE  PostJpaEntity p
        SET     p.likeCount = p.likeCount - 1
        WHERE   p.id = :postId
    """)
    fun decreaseLikeCount(postId: Long)
}
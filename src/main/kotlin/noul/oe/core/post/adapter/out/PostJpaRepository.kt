package noul.oe.core.post.adapter.out

import org.springframework.data.jpa.repository.JpaRepository

interface PostJpaRepository : JpaRepository<PostJpaEntity, Long>
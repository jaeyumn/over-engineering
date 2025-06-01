package noul.oe.core.user.adapter.out.persistence

import jakarta.persistence.*
import noul.oe.core.user.domain.model.Role
import noul.oe.core.user.domain.model.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class UserJpaEntity(
    @Id
    @Column(columnDefinition = "CHAR(36)")
    val id: String = UUID.randomUUID().toString(),

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(nullable = false)
    val modifiedAt: LocalDateTime = LocalDateTime.now(),
) {

    companion object {
        fun create(user: User): UserJpaEntity = UserJpaEntity(
            id = user.id,
            username = user.username,
            email = user.email,
            password = user.password,
            role = user.role,
            createdAt = user.createdAt,
            modifiedAt = user.modifiedAt,
        )
    }
}
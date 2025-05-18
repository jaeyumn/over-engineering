package noul.oe.domain.user.entity

import jakarta.persistence.*
import noul.oe.support.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "users")
class User(
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
    val role: Role = Role.USER

) : BaseEntity()
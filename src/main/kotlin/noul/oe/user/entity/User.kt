package noul.oe.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import noul.oe.common.entity.BaseEntity
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(columnDefinition = "CHAR(36)")
    val id: String = UUID.randomUUID().toString(),

    val username: String,

    val email: String,

    val password: String,

    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER

) : BaseEntity()
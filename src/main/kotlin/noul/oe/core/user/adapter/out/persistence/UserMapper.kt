package noul.oe.core.user.adapter.out.persistence

import noul.oe.core.user.domain.User

object UserMapper {
    fun toEntity(user: User): UserJpaEntity =
        UserJpaEntity(
            id = user.id,
            username = user.username,
            email = user.email,
            password = user.password,
            role = user.role,
            createdAt = user.createdAt,
            modifiedAt = user.modifiedAt,
        )

    fun toDomain(entity: UserJpaEntity): User =
        User (
            id = entity.id,
            username = entity.username,
            email = entity.email,
            password = entity.password,
            role = entity.role,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
}
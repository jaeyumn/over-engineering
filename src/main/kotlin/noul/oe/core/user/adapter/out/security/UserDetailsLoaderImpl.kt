package noul.oe.core.user.adapter.out.security

import noul.oe.core.user.adapter.out.persistence.UserJpaRepository
import noul.oe.core.user.application.exception.UserNotFoundException
import noul.oe.support.security.UserDetailsLoader
import noul.oe.support.security.UserPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class UserDetailsLoaderImpl(
    private val userRepository: UserJpaRepository
) : UserDetailsLoader, UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UserNotFoundException(username)

        return UserPrincipal(
            userId = user.id,
            username = user.username,
            password = user.password,
            authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
    }
}
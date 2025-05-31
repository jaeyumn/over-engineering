package noul.oe.domain.user.infra

import noul.oe.domain.user.repository.UserRepository
import noul.oe.support.security.UserDetailsLoader
import noul.oe.support.security.UserPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserDetailsLoaderImpl(
    private val userRepository: UserRepository
) : UserDetailsLoader, UserDetailsService {

    override fun loadUserByUsername(username: String): UserPrincipal {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: username=$username")

        return UserPrincipal(
            userId = user.id,
            username = user.username,
            password = user.password,
            authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
    }
}
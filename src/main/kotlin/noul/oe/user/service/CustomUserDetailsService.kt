package noul.oe.user.service

import noul.oe.user.entity.User
import noul.oe.user.exception.UserNotFoundException
import noul.oe.user.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UserNotFoundException("User not found: username=$username")
        return CustomUserDetails(user)
    }
}

class CustomUserDetails(
    private val user: User
) : UserDetails {
    fun getUserId(): String = user.id

    override fun getUsername(): String = user.username
    override fun getPassword(): String = user.password
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
}
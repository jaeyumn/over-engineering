package noul.oe.support.security

import org.springframework.security.core.userdetails.UserDetails

interface UserDetailsLoader {
    fun loadUserByUsername(username: String) : UserDetails
}
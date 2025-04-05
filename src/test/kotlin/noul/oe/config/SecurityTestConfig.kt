package noul.oe.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
class SecurityTestConfig {

    @Bean
    fun testSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() } // CSRF 보호 비활성화
        return http.build()
    }
}
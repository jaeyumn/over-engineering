package noul.oe.util

import org.springframework.security.test.context.support.WithSecurityContext

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory::class)
annotation class WithMockCustomUser(
    val userId: String = "testId",
    val username: String = "testUser",
    val password: String = "password",
    val roles: Array<String> = ["ROLE_USER"]
)
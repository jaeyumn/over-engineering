package noul.oe.core.user.adapter

import noul.oe.core.user.adapter.out.persistence.UserJpaRepository
import noul.oe.core.user.adapter.out.persistence.UserMapper
import noul.oe.core.user.adapter.out.persistence.UserRepositoryAdapter
import noul.oe.core.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@Import(UserRepositoryAdapter::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryAdapterTest {

    @Autowired
    lateinit var sut: UserRepositoryAdapter

    @Autowired
    lateinit var userJpaRepository: UserJpaRepository

    private fun createUser(
        id: String = UUID.randomUUID().toString(),
        username: String = "testUser",
        email: String = "test@test.com",
        password: String = "encodedPassword"
    ): User {
        val now = LocalDateTime.now()
        return User(
            id = id,
            username = username,
            email = email,
            password = password,
            createdAt = now,
            modifiedAt = now,
        )
    }

    @Test
    fun saveTest() {
        // given
        val user = createUser()

        // when
        sut.save(user)

        // then
        val saved = userJpaRepository.findById(user.id).orElse(null)
        assertThat(saved).isNotNull
        assertThat(saved.username).isEqualTo(user.username)
        assertThat(saved.email).isEqualTo(user.email)
    }

    @Test
    fun findByUsernameTest() {
        // given
        val user = createUser()
        userJpaRepository.save(UserMapper.toEntity(user))

        // when
        val result = sut.findByUsername(user.username)

        // then
        assertThat(result).isNotNull
        assertThat(result!!.id).isEqualTo(user.id)
        assertThat(result.username).isEqualTo(user.username)
    }

    @Test
    fun existsByEmailTest() {
        // given
        val user = createUser()
        userJpaRepository.save(UserMapper.toEntity(user))

        // when
        val exists = sut.existsByEmail(user.email)
        val notExists = sut.existsByEmail("noneExists@test.com")

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }

    @Test
    fun existsByUsernameTest() {
        // given
        val user = createUser()
        userJpaRepository.save(UserMapper.toEntity(user))

        // when
        val exists = sut.existsByUsername(user.username)
        val notExists = sut.existsByUsername("noneUser")

        // then
        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }
}
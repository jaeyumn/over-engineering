package noul.oe.support.cache

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration

@SpringBootTest
class RedisCacheUtilTest @Autowired constructor(
    private val redisCacheUtil: RedisCacheUtil,
) {
    @Test
    @DisplayName("캐시에 없으면 로딩해서 저장하고, 있으면 캐시에서 가져온다")
    fun test1() {
        val key = "test:user:123"
        redisCacheUtil.remove(key) // 캐시 초기화

        var callCount = 0;
        val result1 = redisCacheUtil.getOrLoad(key, Duration.ofSeconds(60)) {
            callCount++
            "testuser"
        }
        val result2 = redisCacheUtil.getOrLoad(key, Duration.ofSeconds(60)) {
            callCount++
            "testuser"
        }

        assertThat(result1).isEqualTo("testuser")
        assertThat(result2).isEqualTo("testuser")
        assertThat(callCount).isEqualTo(1)
    }
}
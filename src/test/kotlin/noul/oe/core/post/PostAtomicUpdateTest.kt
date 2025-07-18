package noul.oe.core.post

import noul.oe.core.post.application.port.output.PostRepositoryPort
import noul.oe.core.post.application.usecase.PostCommandUseCase
import noul.oe.core.post.application.usecase.PostLikeCommandUseCase
import noul.oe.core.post.domain.Post
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@ActiveProfiles("test")
class PostAtomicUpdateTest @Autowired constructor(
    private val postRepositoryPort: PostRepositoryPort,
    private val postCommandUseCase: PostCommandUseCase,
    private val postLikeCommandUseCase: PostLikeCommandUseCase,
){
    private lateinit var post: Post

    @BeforeEach
    @Transactional
    fun setUp() {
        post = Post(
            id = 1L,
            userId = "testUser",
            title = "title",
            content = "content",
        )
        postRepositoryPort.save(post)
    }

    @Test
    @DisplayName("조회수 증가 요청 동시 발생 시, 최종 조회수가 정확히 반영된다")
    fun atomicIncreaseViewTest() {
        val postId = post.id!!
        val numberOfThreads = 100
        val service = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)

        repeat(numberOfThreads) {
            service.submit {
                try {
                    postCommandUseCase.increaseViewCount(postId)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        service.shutdown()

        val updatedPost = postRepositoryPort.findById(postId)!!

        assertThat(updatedPost.viewCount).isEqualTo(post.viewCount + numberOfThreads)
    }
}
package noul.oe.core.post.adapter.`in`.web.api

import jakarta.validation.Valid
import noul.oe.core.post.application.port.input.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostApiController(
    private val postCommandPort: PostCommandPort,
    private val postLikeCommandPort: PostLikeCommandPort,
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: PostCreateRequest,
    ): ResponseEntity<Void> {
        val command = CreatePostCommand(request.title, request.content)
        postCommandPort.create(command)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/{postId}")
    fun modify(
        @PathVariable postId: Long,
        @Valid @RequestBody request: PostModifyRequest,
    ): ResponseEntity<Void> {
        val command = ModifyPostCommand(postId, request.title, request.content)
        postCommandPort.modify(command)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{postId}")
    fun remove(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postCommandPort.remove(postId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{postId}/like")
    fun like(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postLikeCommandPort.like(postId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{postId}/like")
    fun unlike(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postLikeCommandPort.unlike(postId)
        return ResponseEntity.noContent().build()
    }
}
package noul.oe.core.post.adapter.`in`.web.api

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostApiController(
    private val postService: PostService,
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: PostCreateRequest,
    ): ResponseEntity<Void> {
        postService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/{postId}")
    fun modify(
        @PathVariable postId: Long,
        @Valid @RequestBody request: PostModifyRequest,
    ): ResponseEntity<Void> {
        postService.modify(postId, request)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{postId}")
    fun remove(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postService.remove(postId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{postId}/like")
    fun like(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postService.like(postId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{postId}/like")
    fun unlike(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postService.unlike(postId)
        return ResponseEntity.noContent().build()
    }
}
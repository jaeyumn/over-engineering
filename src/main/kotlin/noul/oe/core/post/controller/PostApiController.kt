package noul.oe.core.post.controller

import jakarta.validation.Valid
import noul.oe.core.post.dto.request.PostCreateRequest
import noul.oe.core.post.dto.request.PostModifyRequest
import noul.oe.core.post.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostApiController(
    private val postService: PostService,
) {
    /**
     * 게시글 생성
     */
    @PostMapping
    fun create(
        @Valid @RequestBody request: PostCreateRequest,
    ): ResponseEntity<Void> {
        postService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{postId}")
    fun modify(
        @PathVariable postId: Long,
        @Valid @RequestBody request: PostModifyRequest,
    ): ResponseEntity<Void> {
        postService.modify(postId, request)
        return ResponseEntity.noContent().build()
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    fun remove(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postService.remove(postId)
        return ResponseEntity.noContent().build()
    }

    /**
     * 게시글 좋아요
     */
    @PostMapping("/{postId}/like")
    fun like(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postService.like(postId)
        return ResponseEntity.noContent().build()
    }

    /**
     * 게시글 좋아요 취소
     */
    @DeleteMapping("/{postId}/like")
    fun unlike(
        @PathVariable postId: Long,
    ): ResponseEntity<Void> {
        postService.unlike(postId)
        return ResponseEntity.noContent().build()
    }
}
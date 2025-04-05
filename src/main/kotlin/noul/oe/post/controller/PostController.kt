package noul.oe.post.controller

import jakarta.validation.Valid
import noul.oe.common.response.ApiResponse
import noul.oe.post.dto.request.PostCreateRequest
import noul.oe.post.dto.request.PostModifyRequest
import noul.oe.post.dto.response.PostDetailResponse
import noul.oe.post.dto.response.PostPageResponse
import noul.oe.post.service.PostService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService
) {
    /**
     * 게시글 생성
     */
    @PostMapping
    fun create(@Valid @RequestBody request: PostCreateRequest, principal: Principal): ResponseEntity<ApiResponse<Nothing>> {
        postService.create(principal.name, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success())
    }

    /**
     * 게시글 단건 조회
     */
    @GetMapping("/{postId}")
    fun read(@PathVariable postId: Long): ResponseEntity<ApiResponse<PostDetailResponse>> {
        val resposne = postService.read(postId)
        return ResponseEntity.ok(ApiResponse.success(resposne))
    }

    /**
     * 게시글 목록 조회(페이징)
     */
    @GetMapping
    fun readAll(pageable: Pageable): ResponseEntity<ApiResponse<Page<PostPageResponse>>> {
        val response = postService.readAll(pageable)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{postId}")
    fun modify(
        @PathVariable postId: Long,
        @Valid @RequestBody request: PostModifyRequest,
        principal: Principal
    ): ResponseEntity<ApiResponse<Nothing>> {
        postService.modify(principal.name, postId, request)
        return ResponseEntity.ok(ApiResponse.success())
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    fun remove(@PathVariable postId: Long, principal: Principal): ResponseEntity<ApiResponse<Nothing>> {
        postService.remove(principal.name, postId)
        return ResponseEntity.ok(ApiResponse.success())
    }

    /**
     * 게시글 좋아요
     */
    @PostMapping("/{postId}/like")
    fun like(@PathVariable postId: Long, principal: Principal): ResponseEntity<ApiResponse<Nothing>> {
        postService.like(principal.name, postId)
        return ResponseEntity.ok(ApiResponse.success())
    }

    /**
     * 게시글 좋아요 취소
     */
    @DeleteMapping("/{postId}/like")
    fun unlike(@PathVariable postId: Long, principal: Principal): ResponseEntity<ApiResponse<Nothing>> {
        postService.unlike(principal.name, postId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}
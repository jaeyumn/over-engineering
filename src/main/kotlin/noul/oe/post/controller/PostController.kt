package noul.oe.post.controller

import jakarta.validation.Valid
import noul.oe.common.response.ApiResponse
import noul.oe.post.dto.request.PostCreateRequest
import noul.oe.post.dto.request.PostModifyRequest
import noul.oe.post.dto.response.PostDetailResponse
import noul.oe.post.dto.response.PostPageResponse
import noul.oe.post.service.PostService
import noul.oe.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService,
    private val userService: UserService
) {
    /**
     * 게시글 생성
     */
    @PostMapping
    fun create(
        @Valid @RequestBody request: PostCreateRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = userService.getUserIdByUsername(user.username)
        postService.create(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success())
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    fun read(
        @PathVariable postId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<PostDetailResponse>> {
        val userId = userService.getUserIdByUsername(user.username)
        val resposne = postService.read(postId, userId)
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
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = userService.getUserIdByUsername(user.username)
        postService.modify(userId, postId, request)
        return ResponseEntity.ok(ApiResponse.success())
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    fun remove(
        @PathVariable postId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = userService.getUserIdByUsername(user.username)
        postService.remove(userId, postId)
        return ResponseEntity.ok(ApiResponse.success())
    }

    /**
     * 게시글 좋아요
     */
    @PostMapping("/{postId}/like")
    fun like(
        @PathVariable postId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = userService.getUserIdByUsername(user.username)
        postService.like(userId, postId)
        return ResponseEntity.ok(ApiResponse.success())
    }

    /**
     * 게시글 좋아요 취소
     */
    @DeleteMapping("/{postId}/like")
    fun unlike(
        @PathVariable postId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = userService.getUserIdByUsername(user.username)
        postService.unlike(userId, postId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}
package noul.oe.comment.controller

import jakarta.validation.Valid
import noul.oe.comment.dto.request.CommentCreateRequest
import noul.oe.comment.dto.request.CommentModifyRequest
import noul.oe.comment.dto.response.CommentResponse
import noul.oe.comment.service.CommentService
import noul.oe.common.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api")
class CommentController(
    private val commentService: CommentService
) {
    /**
     * 댓글 생성
     */
    @PostMapping("/posts/{postId}/comments")
    fun create(
        @PathVariable postId: Long,
        @RequestBody @Valid request: CommentCreateRequest,
        principal: Principal
    ): ResponseEntity<ApiResponse<CommentResponse>> {
        val userId = principal.name
        val response = commentService.create(postId, userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response))
    }

    /**
     * 댓글 조회
     */
    @GetMapping("/posts/{postId}/comments")
    fun readAll(@PathVariable postId: Long): ResponseEntity<ApiResponse<List<CommentResponse>>> {
        val response = commentService.readAll(postId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    fun modify(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentModifyRequest,
        principal: Principal
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = principal.name
        commentService.modify(commentId, userId, request.content)
        return ResponseEntity.ok(ApiResponse.success())
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    fun remove(
        @PathVariable commentId: Long,
        principal: Principal
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = principal.name
        commentService.remove(commentId, userId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}
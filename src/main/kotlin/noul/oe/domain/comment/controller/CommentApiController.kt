package noul.oe.domain.comment.controller

import jakarta.validation.Valid
import noul.oe.domain.comment.dto.request.CommentCreateRequest
import noul.oe.domain.comment.dto.request.CommentModifyRequest
import noul.oe.domain.comment.dto.response.CommentResponse
import noul.oe.domain.comment.service.CommentService
import noul.oe.domain.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CommentApiController(
    private val commentService: CommentService,
    private val userService: UserService,
) {
    /**
     * 댓글 생성
     */
    @PostMapping("/posts/{postId}/comments")
    fun create(
        @PathVariable postId: Long,
        @RequestBody @Valid request: CommentCreateRequest,
    ): ResponseEntity<CommentResponse> {
        val response = commentService.create(postId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 답글 생성
     */
    @PostMapping("/comments/{commentId}/replies")
    fun reply(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentCreateRequest,
    ): ResponseEntity<CommentResponse> {
        val response = commentService.reply(commentId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    fun modify(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentModifyRequest,
    ): ResponseEntity<Void> {
        commentService.modify(commentId, request.content)
        return ResponseEntity.noContent().build()
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    fun remove(
        @PathVariable commentId: Long,
    ): ResponseEntity<Void> {
        commentService.remove(commentId)
        return ResponseEntity.noContent().build()
    }
}
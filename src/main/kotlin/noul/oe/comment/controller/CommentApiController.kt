package noul.oe.comment.controller

import jakarta.validation.Valid
import noul.oe.comment.dto.request.CommentCreateRequest
import noul.oe.comment.dto.request.CommentModifyRequest
import noul.oe.comment.dto.response.CommentResponse
import noul.oe.comment.service.CommentService
import noul.oe.user.service.UserService
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
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<CommentResponse> {
        val userId = userService.getUserIdByUsername(user.username)
        val response = commentService.create(postId, userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 답글 생성
     */
    @PostMapping("/comments/{commentId}/replies")
    fun reply(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentCreateRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<CommentResponse> {
        val userId = userService.getUserIdByUsername(user.username)
        val response = commentService.reply(commentId, userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    fun modify(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentModifyRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Void> {
        val userId = userService.getUserIdByUsername(user.username)
        commentService.modify(commentId, userId, request.content)
        return ResponseEntity.noContent().build()
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    fun remove(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Void> {
        val userId = userService.getUserIdByUsername(user.username)
        commentService.remove(commentId, userId)
        return ResponseEntity.noContent().build()
    }
}
package noul.oe.core.comment.adapter.`in`.web

import jakarta.validation.Valid
import noul.oe.core.comment.application.port.input.CommentCommandPort
import noul.oe.core.comment.application.port.input.CreateCommentCommand
import noul.oe.core.comment.application.port.input.ModifyCommentCommand
import noul.oe.core.comment.application.port.input.ReplyCommentCommand
import noul.oe.support.security.SecurityUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CommentApiController(
    private val commentCommandPort: CommentCommandPort,
) {
    @PostMapping("/posts/{postId}/comments")
    fun create(
        @PathVariable postId: Long,
        @RequestBody @Valid request: CommentCreateRequest,
    ): ResponseEntity<Void> {
        val userId = SecurityUtils.getCurrentUser().userId
        val command = CreateCommentCommand(postId, request.content, userId)
        commentCommandPort.create(command)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/comments/{commentId}/replies")
    fun reply(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentCreateRequest,
    ): ResponseEntity<Void> {
        val command = ReplyCommentCommand(commentId, request.content)
        commentCommandPort.reply(command)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/comments/{commentId}")
    fun modify(
        @PathVariable commentId: Long,
        @RequestBody @Valid request: CommentModifyRequest,
    ): ResponseEntity<Void> {
        val command = ModifyCommentCommand(commentId, request.content)
        commentCommandPort.modify(command)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/comments/{commentId}")
    fun remove(
        @PathVariable commentId: Long,
    ): ResponseEntity<Void> {
        commentCommandPort.remove(commentId)
        return ResponseEntity.noContent().build()
    }
}
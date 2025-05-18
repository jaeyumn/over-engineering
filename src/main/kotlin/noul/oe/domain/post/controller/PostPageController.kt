package noul.oe.domain.post.controller

import noul.oe.domain.comment.service.CommentService
import noul.oe.domain.post.exception.PostPermissionDeniedException
import noul.oe.domain.post.service.PostService
import noul.oe.domain.user.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@Controller
class PostPageController(
    private val postService: PostService,
    private val commentService: CommentService,
    private val userService: UserService,
) {
    /**
     * 게시글 목록 조회(페이징)
     */
    @GetMapping("/posts")
    fun readAll(
        model: Model,
        principal: Principal,
        @PageableDefault(size = 4, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): String {
        val posts = postService.readAll(pageable)
        model.addAttribute("posts", posts)
        model.addAttribute("username", principal.name)

        return "post-list"
    }

    /**
     * 게시글 상세 조회 (댓글 포함)
     */
    @GetMapping("/posts/{postId}")
    fun readDetail(@PathVariable postId: Long, model: Model, principal: Principal): String {
        val userId = userService.getUserIdByUsername(principal.name)
        val post = postService.read(postId, userId)
        val comments = commentService.readAll(postId, userId)
        model.addAttribute("post", post)
        model.addAttribute("comments", comments)
        model.addAttribute("username", principal.name)

        return "post-detail"
    }

    @GetMapping("/posts/write")
    fun write(model: Model, principal: Principal): String {
        model.addAttribute("username", principal.name)

        return "post-write"
    }

    @GetMapping("/posts/{postId}/edit")
    fun modify(@PathVariable postId: Long, model: Model, principal: Principal): String {
        val userId = userService.getUserIdByUsername(principal.name)
        val post = postService.read(postId, userId)

        if (!post.editable) {
            throw PostPermissionDeniedException("Post permission denied by: userId=$userId")
        }

        model.addAttribute("post", post)

        return "post-edit"
    }
}
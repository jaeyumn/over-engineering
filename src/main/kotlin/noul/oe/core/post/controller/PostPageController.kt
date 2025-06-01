package noul.oe.core.post.controller

import noul.oe.core.post.exception.PostPermissionDeniedException
import noul.oe.core.post.service.PostService
import noul.oe.support.security.SecurityUtils
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
) {
    /**
     * 게시글 목록 조회(페이징)
     */
    @GetMapping("/posts")
    fun readAll(
        model: Model,
        @PageableDefault(size = 4, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): String {
        val username = SecurityUtils.getCurrentUser().username
        val posts = postService.readAll(pageable)
        model.addAttribute("posts", posts)
        model.addAttribute("username", username)

        return "post-list"
    }

    /**
     * 게시글 상세 조회 (댓글 포함)
     */
    @GetMapping("/posts/{postId}")
    fun readDetail(@PathVariable postId: Long, model: Model): String {
        val user = SecurityUtils.getCurrentUser()
        val postWithComments = postService.readWithComments(postId, user)
        model.addAttribute("post", postWithComments.post)
        model.addAttribute("comments", postWithComments.comments)
        model.addAttribute("username", user.username)

        return "post-detail"
    }

    @GetMapping("/posts/write")
    fun write(model: Model, principal: Principal): String {
        model.addAttribute("username", principal.name)

        return "post-write"
    }

    @GetMapping("/posts/{postId}/edit")
    fun modify(@PathVariable postId: Long, model: Model): String {
        val user = SecurityUtils.getCurrentUser()
        val post = postService.read(postId, user)

        if (!post.editable) {
            throw PostPermissionDeniedException("Post permission denied by: userId=$user.userId")
        }

        model.addAttribute("post", post)

        return "post-edit"
    }
}
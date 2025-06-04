package noul.oe.core.post.adapter.`in`.web.page

import noul.oe.core.post.application.exception.PostPermissionDeniedException
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
            throw PostPermissionDeniedException(user.userId)
        }

        model.addAttribute("post", post)

        return "post-edit"
    }
}
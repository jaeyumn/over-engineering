package noul.oe.post.dto.response

import noul.oe.post.entity.Post

data class PostPageResponse(
    val postId: Long,
    val title: String,
) {
    companion object {
        fun from(post: Post): PostPageResponse = PostPageResponse(
            postId = post.id,
            title = post.title,
        )
    }
}
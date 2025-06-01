package noul.oe.support.port.dto

data class CommentInfo(
    val id: Long,
    val content: String,
    val userId: String,
    val username: String = "",
    val createdAt: String,
    val editable: Boolean,
)

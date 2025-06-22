package noul.oe.support.redis

enum class CacheKey(private val prefix: String) {
    COMMENT_COUNT("comment:commentCount:"),
    USERNAME("user:username:");

    fun with(id: Long): String {
        return "$prefix:$id"
    }

    fun with(id: String): String {
        return "$prefix:$id"
    }

    fun lockFor(id: Long): String {
        return "${prefix}Lock:$id"
    }

    fun lockFor(id: String): String {
        return "${prefix}Lock:$id"
    }
}
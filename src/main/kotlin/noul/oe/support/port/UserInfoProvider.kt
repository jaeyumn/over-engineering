package noul.oe.support.port

interface UserInfoProvider {
    fun getUsername(userId: String): String
}
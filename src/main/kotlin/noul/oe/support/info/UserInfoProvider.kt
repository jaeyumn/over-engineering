package noul.oe.support.info

interface UserInfoProvider {
    fun getUsername(userId: String): String
}
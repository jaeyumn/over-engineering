package noul.oe.core.user.application.port.input

interface AuthCommandPort {
    fun login(command: LoginCommand)
}
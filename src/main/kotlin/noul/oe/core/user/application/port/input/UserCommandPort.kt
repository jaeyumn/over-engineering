package noul.oe.core.user.application.port.input

interface UserCommandPort {
    fun signUp(command: SignUpCommand)
}
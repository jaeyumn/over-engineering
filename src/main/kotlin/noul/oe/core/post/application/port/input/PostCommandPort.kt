package noul.oe.core.post.application.port.input

interface PostCommandPort {
    fun create(command: CreateCommand)
    fun modify(command: ModifyCommand)
    fun remove(command: RemoveCommand)
}
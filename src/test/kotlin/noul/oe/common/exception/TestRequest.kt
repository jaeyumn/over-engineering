package noul.oe.common.exception

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class TestRequest(
    @field:NotBlank(message = "이름은 필수입니다")
    val name: String?,
    @field:Min(value = 1, message = "나이는 1 이상이어야 합니다")
    val age: Int?
)

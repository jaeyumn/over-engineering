package noul.oe.core.user.adapter.`in`.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthPageController {
    @GetMapping("/")
    fun home(): String = "redirect:/login"

    @GetMapping("/login")
    fun loginPage(): String = "login"

    @GetMapping("/signup")
    fun signup(): String = "signup"
}
package noul.oe.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthPageController {

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/")
    fun home(): String {
        return "redirect:/login"
    }

    @GetMapping("/signup")
    fun signup(): String {
        return "signup"
    }
}
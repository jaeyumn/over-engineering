package noul.oe.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal

@Controller
class AuthPageController {

    @GetMapping("/login")
    fun loginPage(@RequestParam(value = "error", required = false) error: String?, model: Model): String {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.")
        }
        return "login"
    }

    @GetMapping("/")
    fun home(): String {
        return "redirect:/login"
    }
}
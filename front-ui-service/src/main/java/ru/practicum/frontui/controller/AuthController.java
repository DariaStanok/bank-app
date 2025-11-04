package ru.practicum.frontui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.dto.RegisterRequestDto;
import ru.practicum.frontui.service.FrontUiService;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class AuthController {

    private final FrontUiService front;

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/logout")
    public RedirectView logout() { return new RedirectView("/auth/logout"); }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterRequestDto());
        return "register";
    }

    @PostMapping("/register")
    public RedirectView registerSubmit(@ModelAttribute("form") RegisterRequestDto form) {
        front.register(form);
        return new RedirectView("/ui/login");
    }
}

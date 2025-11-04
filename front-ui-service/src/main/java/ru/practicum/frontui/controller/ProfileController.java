package ru.practicum.frontui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.service.FrontUiService;
import ru.practicum.platform.contracts.accounts.ChangePasswordDto;
import ru.practicum.platform.contracts.accounts.UpdateUserAccount;
import ru.practicum.platform.contracts.accounts.UserViewDto;

@Controller
@RequestMapping("/ui/profile")
@RequiredArgsConstructor
public class ProfileController {
	
    private final FrontUiService front;

    @GetMapping
    public String page(@RequestParam("userId") Long userId, Model model) {
        UserViewDto snapshot = front.getUserSnapshot(userId);
        model.addAttribute("userId", userId);
        model.addAttribute("user", snapshot);
        return "profile";
    }

    @PostMapping("/update")
    public RedirectView updateProfile(@RequestParam("userId") Long userId,
                                      UpdateUserAccount dto) {
        front.updateProfile(userId, dto);
        return new RedirectView("/ui/profile?userId=" + userId);
    }

    @PostMapping("/password")
    public RedirectView changePassword(@RequestParam("userId") Long userId,
                                       @RequestParam("email") String email,
                                       @RequestParam("newPassword") String newPassword) {
        front.changePassword(userId, new ChangePasswordDto(email, newPassword));
        return new RedirectView("/ui/profile?userId=" + userId);
    }

    @PostMapping("/delete")
    public RedirectView deleteAccount(@RequestParam("userId") Long userId) {
        front.deleteUser(userId);
        return new RedirectView("/ui/login");
    }
}

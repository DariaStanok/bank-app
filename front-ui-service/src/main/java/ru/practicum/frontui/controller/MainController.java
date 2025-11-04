package ru.practicum.frontui.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.service.FrontUiService;
import ru.practicum.platform.contracts.accounts.UserViewDto;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class MainController {
	
    private final FrontUiService front;

    @GetMapping({"/", "/main"})
    public String main(@RequestParam("userId") Long userId, Model model) {
        UserViewDto snapshot = front.getUserSnapshot(userId);
        List<SendNotificationRequest> notifications = front.getRecentNotifications(userId, 10);

        model.addAttribute("userId", userId);
        model.addAttribute("user", snapshot);
        model.addAttribute("notifications", notifications);
        return "main";
    }
}

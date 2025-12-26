package ru.practicum.frontui.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.integration.AccountsClient;
import ru.practicum.frontui.service.FrontUiService;
import ru.practicum.platform.contracts.accounts.UserViewDto;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class MainController {
	
	private final FrontUiService front;
    private final AccountsClient accountsClient;

    @GetMapping({"/", "/main"})
    public String main(Authentication authentication, Model model) {

        String username = authentication.getName();
        Long userId = accountsClient.resolveUserId(username);

        UserViewDto snapshot = front.getUserSnapshot(userId);
        List<SendNotificationRequest> notifications =
                front.getRecentNotifications(userId, 10);

        model.addAttribute("userId", userId);
        model.addAttribute("user", snapshot);
        model.addAttribute("notifications", notifications);

        return "main";
    }
   
}

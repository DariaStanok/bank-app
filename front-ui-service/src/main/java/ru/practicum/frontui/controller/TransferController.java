package ru.practicum.frontui.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.service.FrontUiService;

@Controller
@RequestMapping("/ui/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final FrontUiService front;

    @GetMapping
    public String page(@RequestParam Long userId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("accounts", front.getAccounts(userId));
        return "transfer"; // если нужна отдельная страница
    }

    @PostMapping
    public RedirectView submit(@RequestParam Long userId,
                               @RequestParam Long fromAccountId,
                               @RequestParam Long toAccountId,
                               @RequestParam BigDecimal amount,
                               @RequestParam(value = "operationId", required = false) String opId) {
        front.transfer(fromAccountId, toAccountId, amount, opId);
        return new RedirectView("/ui/main?userId=" + userId);
    }
}


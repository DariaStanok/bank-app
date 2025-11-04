package ru.practicum.frontui.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.service.FrontUiService;
import ru.practicum.platform.contracts.cash.CashChangeDto;
import ru.practicum.platform.contracts.enums.Currency;

@Controller
@RequestMapping("/ui/balance")
@RequiredArgsConstructor
public class BalanceController {
	
    private final FrontUiService front;

    @PostMapping("/deposit")
    public RedirectView deposit(@RequestParam Long userId,
                                @RequestParam Long accountId,
                                @RequestParam BigDecimal amount,
                                @RequestParam Currency currency,
                                @RequestParam(value = "operationId", required = false) String opId) {
        front.deposit(new CashChangeDto(accountId, currency, amount), opId);
        return new RedirectView("/ui/main?userId=" + userId);
    }

    @PostMapping("/withdraw")
    public RedirectView withdraw(@RequestParam Long userId,
                                 @RequestParam Long accountId,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam Currency currency,
                                 @RequestParam(value = "operationId", required = false) String opId) {
        front.withdraw(new CashChangeDto(accountId, currency, amount), opId);
        return new RedirectView("/ui/main?userId=" + userId);
    }
}

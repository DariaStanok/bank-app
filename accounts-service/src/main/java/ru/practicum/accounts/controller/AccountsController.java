package ru.practicum.accounts.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.practicum.accounts.service.AccountsService;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.BalanceChangeDto;
import ru.practicum.platform.contracts.accounts.NewBalanceDto;
import ru.practicum.platform.contracts.accounts.UpdateExternalAccount;
import ru.practicum.platform.contracts.accounts.UserViewDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AccountsController {

    private final AccountsService accountsService;

    @GetMapping("/resolve")
    public Long resolveUserId(@RequestParam("username") String username) {
        return accountsService.resolveUserIdByUsername(username);
    }

    @GetMapping("/{userId}/view")
    public UserViewDto getUserSnapshot(@PathVariable Long userId) {
        return accountsService.getUserSnapshot(userId);
    }

    @GetMapping("/{userId}/accounts")
    public List<AccountView> getAccounts(@PathVariable Long userId) {
        return accountsService.getUserAccounts(userId);
    }

    @PostMapping("/{userId}/accounts")
    public AccountView createAccount(@PathVariable Long userId,
                                     @RequestBody @Valid UpdateExternalAccount dto) {
        return accountsService.createAccount(userId, dto);
    }

    @PutMapping("/{userId}/accounts/{accountId}")
    public AccountView updateAccount(@PathVariable Long userId,
                                     @PathVariable Long accountId,
                                     @RequestBody @Valid UpdateExternalAccount dto) {
        return accountsService.updateAccount(userId, accountId, dto);
    }

    @DeleteMapping("/{userId}/accounts/{accountId}")
    public Boolean deleteAccount(@PathVariable Long userId, @PathVariable Long accountId) {
        accountsService.deleteAccount(userId, accountId);
        return Boolean.TRUE;
    }

    @PostMapping("/{userId}/accounts/{accountId}/balance-change")
    public NewBalanceDto changeBalance(@PathVariable Long userId,
                                       @PathVariable Long accountId,
                                       @RequestBody @Valid BalanceChangeDto dto) {
        return accountsService.changeBalance(accountId, dto);
    }
}


package ru.practicum.platform.contracts.accounts;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public record UserViewDto(
        @NotNull @Valid UserDto user,
       @Valid AccountView currentAccount,
       @NotNull List<AccountView> accounts
) {}

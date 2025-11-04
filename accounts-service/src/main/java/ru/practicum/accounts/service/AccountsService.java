package ru.practicum.accounts.service;

import java.util.List;

import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.BalanceChangeDto;
import ru.practicum.platform.contracts.accounts.NewBalanceDto;
import ru.practicum.platform.contracts.accounts.UpdateExternalAccount;
import ru.practicum.platform.contracts.accounts.UserViewDto;

public interface AccountsService {
	
	UserViewDto getUserSnapshot(Long userId);
	
    List<AccountView> getUserAccounts(Long userId);
    
    AccountView createAccount(Long userId, UpdateExternalAccount dto);
    
    AccountView updateAccount(Long userId, Long accountId, UpdateExternalAccount dto);
    
    void deleteAccount(Long userId, Long accountId);
    
 	NewBalanceDto changeBalance(Long accountId, BalanceChangeDto dto);


}

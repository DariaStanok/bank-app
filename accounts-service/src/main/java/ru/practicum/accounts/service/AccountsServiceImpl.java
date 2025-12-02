package ru.practicum.accounts.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.practicum.accounts.model.Account;
import ru.practicum.accounts.model.UserAccount;
import ru.practicum.accounts.repository.AccountRepository;
import ru.practicum.accounts.repository.UserAccountRepository;
import ru.practicum.kafka.starter.NotificationProducer;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.BalanceChangeDto;
import ru.practicum.platform.contracts.accounts.NewBalanceDto;
import ru.practicum.platform.contracts.accounts.UpdateExternalAccount;
import ru.practicum.platform.contracts.accounts.UserDto;
import ru.practicum.platform.contracts.accounts.UserViewDto;
import ru.practicum.platform.contracts.enums.NotificationEvent;
import ru.practicum.platform.contracts.notifications.NotificationMessageDto;
import ru.practicum.web.exception.BadRequestException;
import ru.practicum.web.exception.ConflictException;
import ru.practicum.web.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements AccountsService {

    private final UserAccountRepository users;
    private final AccountRepository accounts;
    private final NotificationProducer notificationProducer;
    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public UserViewDto getUserSnapshot(Long userId) {
    	UserAccount user = loadUser(userId);

        List<AccountView> accountViews = accounts.findAllByUserId(userId)
                .stream()
                .map(a -> mapper.map(a, AccountView.class))
                .toList();

        AccountView current = null;
        Long currentId = user.getCurrentAccountId();
        if (currentId != null) {
            current = accountViews.stream()
                    .filter(v -> Objects.equals(v.id(), currentId))
                    .findFirst()
                    .orElse(null);
        }

        UserDto userDto = mapper.map(user, UserDto.class);
        return new UserViewDto(userDto, current,accountViews
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountView> getUserAccounts(Long userId) {
        return accounts.findAllByUserId(userId)
                .stream()
                .map(a -> mapper.map(a, AccountView.class))
                .toList();
    }

    @Override
    @Transactional
    public AccountView createAccount(Long userId, UpdateExternalAccount dto) {
        UserAccount user = loadUser(userId);
        if (accounts.findByUserIdAndCurrency(userId, dto.currency()).isPresent()) {
            throw new BadRequestException("ONLY_ONE_ACCOUNT_PER_CURRENCY");
        }

        Account acc = Account.builder()
                .user(user)
                .externalAccountId(dto.externalAccountId())
                .currency(dto.currency())
                .balance(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .build();

        acc = accounts.save(acc);
        if (user.getCurrentAccountId() == null) {
            user.setCurrentAccountId(acc.getId());
            users.save(user);
        }

        notificationProducer.send(
                NotificationMessageDto.builder()
                        .event(NotificationEvent.ACCOUNT_CREATED)
                        .userId(user.getId())
                        .accountId(acc.getId())
                        .message("Account created: " + dto.currency())
                        .at(Instant.now())
                        .build()
        );

        return mapper.map(acc, AccountView.class);
    }

    @Override
    @Transactional
    public AccountView updateAccount(Long userId, Long accountId, UpdateExternalAccount dto) {
        Account acc = accounts.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND"));
        if (!Objects.equals(acc.getCurrency(), dto.currency())
                && accounts.findByUserIdAndCurrency(userId, dto.currency()).isPresent()) {
            throw new BadRequestException("ONLY_ONE_ACCOUNT_PER_CURRENCY");
        }

        acc.setExternalAccountId(dto.externalAccountId());
        acc.setCurrency(dto.currency());
        accounts.save(acc);

        notificationProducer.send(
                NotificationMessageDto.builder()
                .event(NotificationEvent.ACCOUNT_UPDATED)
                .userId(acc.getUser().getId())
                .accountId(acc.getId())
                .message("Account updated: " + dto.currency()) 
                .at(Instant.now())
                .build()
        );

        return mapper.map(acc, AccountView.class);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId, Long accountId) {
        Account acc = accounts.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND"));

        BigDecimal balance = acc.getBalance() == null ? BigDecimal.ZERO : acc.getBalance();
        if (balance.signum() != 0) {
            throw new BadRequestException("CANNOT_DELETE_NON_ZERO_BALANCE");
        }

        UserAccount user = acc.getUser();
        accounts.delete(acc);

        if (Objects.equals(user.getCurrentAccountId(), acc.getId())) {
            user.setCurrentAccountId(null);
            users.save(user);
        }

        notificationProducer.send(
                NotificationMessageDto.builder()
        		.event(NotificationEvent.ACCOUNT_DELETED)
        		.userId(user.getId())
        		.message("Account deleted: " + acc.getCurrency())
        		.at(Instant.now())
                .build()
        );
    }

    @Override
    @Transactional
    public NewBalanceDto changeBalance(Long accountId, BalanceChangeDto dto) {
        Account acc = accounts.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND"));

        if (dto.currency() == null || !dto.currency().equals(acc.getCurrency())) {
            throw new ConflictException("CURRENCY_MISMATCH");
        }
        if (dto.amount() == null || dto.amount().signum() <= 0) {
            throw new BadRequestException("AMOUNT_MUST_BE_POSITIVE");
        }

        BigDecimal current = acc.getBalance() == null ? BigDecimal.ZERO : acc.getBalance();
        BigDecimal next;

        switch (dto.type()) {
            case DEPOSIT -> next = current.add(dto.amount());
            case WITHDRAW -> {
                if (current.compareTo(dto.amount()) < 0) {
                    throw new ConflictException("INSUFFICIENT_FUNDS");
                }
                next = current.subtract(dto.amount());
            }
            default -> throw new BadRequestException("UNKNOWN_BALANCE_CHANGE_TYPE");
        }

        acc.setBalance(next);
        accounts.save(acc);

        return new NewBalanceDto(next, Instant.now());
    }

    private UserAccount loadUser(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
    }

	
}

package ru.practicum.accounts.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.practicum.accounts.model.Account;
import ru.practicum.accounts.model.UserAccount;
import ru.practicum.accounts.repository.AccountRepository;
import ru.practicum.accounts.repository.UserAccountRepository;
import ru.practicum.client.NotificationsClient;
import ru.practicum.platform.contracts.accounts.ChangePasswordDto;
import ru.practicum.platform.contracts.accounts.UpdateUserAccount;
import ru.practicum.platform.contracts.accounts.UserDto;
import ru.practicum.platform.contracts.enums.NotificationEvent;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;
import ru.practicum.web.exception.BadRequestException;
import ru.practicum.web.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserAccountRepository users;
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    private final ModelMapper mapper;
    private final NotificationsClient notifications;

    @Override
    @Transactional(readOnly = true)
    public UserDto getProfile(Long userId) {
        UserAccount user = load(userId);
        return mapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto updateProfile(Long userId, UpdateUserAccount dto) {
        validateAdult(dto.birthDate());

        UserAccount user = load(userId);
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setBirthDate(dto.birthDate());

        users.save(user);

        notifications.send(new SendNotificationRequest(
                NotificationEvent.USER_UPDATED,
                null,
                user.getId(),
                "Profile updated",
                Instant.now()
        ));

        return mapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDto dto) {
        if (dto.newPassword() == null || dto.newPassword().isBlank()) {
            throw new BadRequestException("PASSWORD_MUST_NOT_BE_BLANK");
        }

        UserAccount user = load(userId);
        if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(dto.email())) {
            throw new BadRequestException("EMAIL_VERIFICATION_FAILED");
        }

        user.setPasswordHash(encoder.encode(dto.newPassword()));
        users.save(user);

        notifications.send(new SendNotificationRequest(
                NotificationEvent.USER_PASSWORD_CHANGED,
                null,
                user.getId(),
                "Password changed",
                Instant.now()
        ));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        boolean hasNonZero = accounts.findAllByUserId(userId).stream()
                .map(Account::getBalance)
                .anyMatch(b -> b != null && b.signum() != 0);

        if (hasNonZero) {
            throw new BadRequestException("CANNOT_DELETE_USER_WITH_NON_ZERO_ACCOUNTS");
        }

        users.deleteById(userId);
    }

  
    private UserAccount load(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
    }

    private void validateAdult(LocalDate birth) {
        if (birth == null || Period.between(birth, LocalDate.now()).getYears() < 18) {
            throw new BadRequestException("USER_MUST_BE_18_PLUS");
        }
    }
}



package ru.practicum.accounts.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.practicum.accounts.service.AccountsService;
import ru.practicum.accounts.service.ProfileService;
import ru.practicum.platform.contracts.accounts.ChangePasswordDto;
import ru.practicum.platform.contracts.accounts.UpdateUserAccount;
import ru.practicum.platform.contracts.accounts.UserDto;
import ru.practicum.platform.contracts.accounts.UserViewDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ProfileController {
	
	private final ProfileService profileService;
    private final AccountsService accountsService;
    
    @GetMapping("/{userId}")
    public UserViewDto getUser(@PathVariable Long userId) {
        return accountsService.getUserSnapshot(userId);
    }

    @PutMapping("/{userId}/profile")
    public UserDto updateProfile(@PathVariable Long userId,
                                 @RequestBody @Valid UpdateUserAccount dto) {
        return profileService.updateProfile(userId, dto);
    }

    @PostMapping("/{userId}/profile/password")
    public Boolean changePassword(@PathVariable Long userId,
                                  @RequestBody @Valid ChangePasswordDto dto) {
        profileService.changePassword(userId, dto);
        return Boolean.TRUE;
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable Long userId) {
        profileService.deleteUser(userId);
        return Boolean.TRUE;
    }

}

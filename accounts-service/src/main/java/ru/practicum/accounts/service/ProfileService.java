package ru.practicum.accounts.service;

import ru.practicum.platform.contracts.accounts.ChangePasswordDto;
import ru.practicum.platform.contracts.accounts.UpdateUserAccount;
import ru.practicum.platform.contracts.accounts.UserDto;

public interface ProfileService {
	UserDto getProfile(Long userId);

	UserDto updateProfile(Long userId, UpdateUserAccount dto);

	void changePassword(Long userId, ChangePasswordDto dto);

	void deleteUser(Long userId);

}
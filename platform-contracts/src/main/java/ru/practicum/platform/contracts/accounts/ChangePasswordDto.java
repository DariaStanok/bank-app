package ru.practicum.platform.contracts.accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record ChangePasswordDto ( 
	@Email @NotBlank
	String email,
	@NotBlank
	String newPassword
	) {}

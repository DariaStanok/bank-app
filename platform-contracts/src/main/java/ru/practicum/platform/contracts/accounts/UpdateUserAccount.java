package ru.practicum.platform.contracts.accounts;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateUserAccount (
	@NotBlank
	String firstName,
	@NotBlank
	String lastName,
	@Email
	@NotBlank
	String email,
	@NotNull
	LocalDate birthDate
	) {}

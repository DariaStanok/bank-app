package ru.practicum.frontui.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
	@NotBlank
	private String username;
	@NotBlank
    private String password;
	@Email 
	@NotBlank
    private String email;
    private LocalDate birthDate;
}

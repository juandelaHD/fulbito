package ar.uba.fi.ingsoft1.football5.user.password_reset_token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for requesting a password reset")
public record ForgotPasswordRequestDTO(
        @NotBlank
        @Email
        @Schema(description = "Registered user's email", example = "user@email.com")
        String email
) {}
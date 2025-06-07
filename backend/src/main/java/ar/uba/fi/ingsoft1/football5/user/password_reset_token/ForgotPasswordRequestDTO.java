package ar.uba.fi.ingsoft1.football5.user.password_reset_token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO para solicitar el reseteo de contraseña por email")
public record ForgotPasswordRequestDTO(
        @NotBlank
        @Email
        @Schema(description = "Email del usuario registrado", example = "user@email.com")
        String email
) {}
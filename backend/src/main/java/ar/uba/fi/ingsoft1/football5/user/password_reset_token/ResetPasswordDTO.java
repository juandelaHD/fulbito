package ar.uba.fi.ingsoft1.football5.user.password_reset_token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO to reset the password using a token")
public record ResetPasswordDTO(
        @NotBlank
        @Schema(description = "Password reset token", example = "uuid-token")
        String token,

        @NotBlank
        @Schema(description = "New password", example = "NewPassword123!")
        String newPassword,

        @NotBlank
        @Schema(description = "Confirmation of the new password", example = "NewPassword123!")
        String confirmPassword
) {}
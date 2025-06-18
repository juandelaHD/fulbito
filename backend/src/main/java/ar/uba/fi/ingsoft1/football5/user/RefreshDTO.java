package ar.uba.fi.ingsoft1.football5.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for requesting a new access token using a refresh token.")
public record RefreshDTO(
        @NotBlank(message = "Refresh token must not be blank")
        @Schema(description = "JWT refresh token used to get a new access token", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String refreshToken
) {}

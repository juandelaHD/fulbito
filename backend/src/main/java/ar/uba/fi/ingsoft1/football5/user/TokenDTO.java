package ar.uba.fi.ingsoft1.football5.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO containing authentication tokens.")
public record TokenDTO(
        @NotNull(message = "Access token must not be null")
        @Schema(description = "JWT access token used to authorize user requests", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String accessToken,

        @Schema(description = "Optional JWT refresh token to obtain a new access token", example = "eyJhbGciOiJIUzI1NiIsInR...")
        String refreshToken,

        @Schema(description = "User's role in the system", example = "USER")
        Role role
) {}

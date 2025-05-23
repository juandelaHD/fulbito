package ar.uba.fi.ingsoft1.football5.user;

import jakarta.validation.constraints.NotNull;

public record TokenDTO(
        @NotNull String accessToken,
        String refreshToken
) {
}

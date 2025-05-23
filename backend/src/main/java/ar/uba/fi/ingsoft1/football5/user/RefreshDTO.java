package ar.uba.fi.ingsoft1.football5.user;

import jakarta.validation.constraints.NotBlank;

public record RefreshDTO(
        @NotBlank String refreshToken
) {}

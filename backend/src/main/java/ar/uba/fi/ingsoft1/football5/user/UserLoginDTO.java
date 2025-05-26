package ar.uba.fi.ingsoft1.football5.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
        @NotBlank String username,
        @NotBlank String password
) implements UserCredentials {
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}

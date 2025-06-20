package ar.uba.fi.ingsoft1.football5.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object used for user login requests.")
public record UserLoginDTO(

        @NotBlank(message = "Username must not be blank")
        @Schema(description = "User's unique login name", example = "john_doe")
        String username,

        @NotBlank(message = "Password must not be blank")
        @Schema(description = "User's password in plain text", example = "securePassword123")
        String password

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

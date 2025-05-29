package ar.uba.fi.ingsoft1.football5.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.function.Function;

@Schema(description = "Data Transfer Object used when creating a new user account.")
public record UserCreateDTO(
        @NotBlank(message = "First name is required")
        @Schema(description = "User's first name", example = "John")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Schema(description = "User's last name", example = "Doe")
        String lastName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Username (email) is required")
        @Schema(description = "Valid username (email) address for the user", example = "john.doe@example.com")
        String username,

        @NotBlank(message = "Password is required")
        @Schema(description = "Password for the user's account", example = "securePassword123")
        String password,

        @NotBlank(message = "Zone is required")
        @Schema(description = "User's geographical or organizational zone", example = "Buenos Aires")
        String zone,

        @NotBlank(message = "Age is required")
        @Schema(description = "User's age", example = "22")
        Integer age,

        @NotBlank(message = "Gender is required")
        @Schema(description = "User's gender identity", example = "Male")
        String gender,

        @NotNull(message = "Role is required")
        @Schema(description = "Role assigned to the user within the system", example = "USER")
        Role role
) implements UserCredentials {

    public User asUser(Function<String, String> encryptPassword) {
        return new User(
                username,
                firstName,
                lastName,
                gender,
                zone,
                age,
                encryptPassword.apply(password),
                role
        );
    }

    @Override
    public String getUsername(){
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}

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
        @NotBlank(message = "Email is required")
        @Schema(description = "Valid email address for the user", example = "john.doe@example.com")
        String email,

        @NotBlank(message = "Username is required")
        @Schema(description = "Unique username used to log in", example = "john_doe")
        String username,

        @NotBlank(message = "Password is required")
        @Schema(description = "Password for the user's account", example = "securePassword123")
        String password,

        @NotBlank(message = "Avatar is required")
        @Schema(description = "URL of the user's profile picture", example = "https://example.com/avatar.jpg")
        String avatar,

        @NotBlank(message = "Area is required")
        @Schema(description = "User's geographical or organizational area", example = "Buenos Aires")
        String area,

        @NotBlank(message = "Date of birth is required")
        @Schema(description = "User's date of birth in ISO format (yyyy-MM-dd)", example = "1995-04-20")
        String dateBirth,

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
                email,
                gender,
                avatar,
                area,
                dateBirth,
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

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getArea() {
        return this.area;
    }

    public String getDateBirth() {
        return this.dateBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public Role getRole() {
        return this.role;
    }
}

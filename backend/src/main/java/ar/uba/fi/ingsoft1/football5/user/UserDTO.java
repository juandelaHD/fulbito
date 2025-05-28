package ar.uba.fi.ingsoft1.football5.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object representing a user.")
public record UserDTO(
        @NotNull(message = "ID must not be null")
        @Schema(description = "Unique identifier of the user", example = "123")
        Long id,

        @NotBlank(message = "First name must not be blank")
        @Schema(description = "User's first name", example = "John")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Schema(description = "User's last name", example = "Doe")
        String lastName,

        @NotBlank(message = "Username must not be blank")
        @Schema(description = "User's chosen username", example = "john_doe")
        String username,

        @NotBlank(message = "AvatarId must not be blank")
        @Schema(description = "Avatar's image Id", example = "1")
        Long avatarId,

        @NotBlank(message = "Zone must not be blank")
        @Schema(description = "Geographical or organizational zone of the user", example = "Buenos Aires")
        String zone,

        @NotBlank(message = "Age must not be blank")
        @Schema(description = "User's age", example = "22")
        Integer age,

        @NotBlank(message = "Gender must not be blank")
        @Schema(description = "User's gender", example = "Male")
        String gender,

        @NotNull(message = "Role must not be null")
        @Schema(description = "User's role in the system", example = "USER")
        Role role,

        @Schema(description = "Indicates whether the user's email is confirmed", example = "true")
        boolean emailConfirmed
) {
    public UserDTO(User user) {
        this(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getAvatar().getId(),
                user.getZone(),
                user.getAge(),
                user.getGender(),
                user.getRole(),
                user.isEmailConfirmed()
        );
    }
}

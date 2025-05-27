package ar.uba.fi.ingsoft1.football5.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
        @NotBlank Long id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        Long avatarId,
        @NotBlank String zone,
        @NotBlank Integer age,
        @NotBlank String gender,
        @NotNull Role role,
        @NotBlank boolean emailConfirmed
) {
    UserDTO(User user) {
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
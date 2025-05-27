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
        @NotBlank String dateBirth,
        @NotBlank String gender,
        @NotNull Role role
) {
    UserDTO(User user) {
        this(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getAvatar().getId(),
                user.getZone(),
                user.getDateBirth(),
                user.getGender(),
                user.getRole()
        );
    }
}
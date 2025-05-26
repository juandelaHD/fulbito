package ar.uba.fi.ingsoft1.football5.user;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String avatar,
        @NotBlank String area,
        @NotBlank String dateBirth,
        @NotBlank String gender
) {
    UserDTO(User user) {
        this(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getAvatar(),
                user.getArea(),
                user.getDateBirth(),
                user.getGender()
        );
    }
}
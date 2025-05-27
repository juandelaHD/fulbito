package ar.uba.fi.ingsoft1.football5.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.function.Function;

public record UserCreateDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String zone,
        @NotBlank Integer age,
        @NotBlank String gender,
        @NotNull Role role
) implements UserCredentials {
    public User asUser(Function<String, String> encryptPassword) {
        return new User(firstName, lastName, username, gender, zone, age, encryptPassword.apply(password), role);
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


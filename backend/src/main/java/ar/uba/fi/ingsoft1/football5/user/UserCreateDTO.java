package ar.uba.fi.ingsoft1.football5.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.function.Function;

public record UserCreateDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String avatar,
        @NotBlank String area,
        @NotBlank String dateBirth,
        @NotBlank String gender,
        @NotBlank String role
) implements UserCredentials {
    public User asUser(Function<String, String> encryptPassword) {
        return new User(username, firstName, lastName, email, gender, avatar, area, dateBirth, encryptPassword.apply(password), role);
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

    public String getRole() {
        return this.role;
    }
}


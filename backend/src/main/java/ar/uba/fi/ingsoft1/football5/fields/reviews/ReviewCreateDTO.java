package ar.uba.fi.ingsoft1.football5.fields.reviews;

import jakarta.validation.constraints.*;

public record ReviewCreateDTO(
        @NotNull(message = "Field ID cannot be null")
        @Min(value = 1, message = "Field ID must be greater than 0")
        @Max(value = 10, message = "Field ID must be less than or equal to 10")
        Integer rating,

        @NotBlank(message = "Comment cannot be empty")
        @Size(min = 1, max = 100)
        String comment
) {}

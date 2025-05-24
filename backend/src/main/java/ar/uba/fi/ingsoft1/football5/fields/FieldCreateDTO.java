package ar.uba.fi.ingsoft1.football5.fields;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FieldCreateDTO(
        @NotBlank @Size(min = 1, max = 100) String name,
        @NotNull GrassType grassType,
        @NotNull Boolean illuminated,
        @NotBlank @Size(min = 1, max = 100) String zone,
        @NotBlank @Size(min = 1, max = 100) String address
) {
    public Field asField() {
        return this.asField(null);
    }

    public Field asField(Long id) {
        return new Field(id, this.name, this.grassType, this.illuminated, new Location(this.zone, this.address));
    }
}

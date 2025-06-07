package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.user.User;
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
    public Field asField(User owner) {
        return this.asField(null, owner);
    }

    public Field asField(Long id, User owner) {
        return new Field(id, this.name, this.grassType, this.illuminated, new Location(this.zone, this.address), owner);
    }

    public Field adUpdatedField(Field existing) {
        existing.setName(this.name().toLowerCase());
        existing.setGrassType(this.grassType());
        existing.setIlluminated(this.illuminated());
        existing.setLocation(new Location(this.zone().toLowerCase(), this.address().toLowerCase()));
        return existing;
    }
}

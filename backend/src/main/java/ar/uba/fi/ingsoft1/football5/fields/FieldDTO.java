package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.images.Image;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record FieldDTO(

        @Schema(description = "Match's ID", example = "1234")
        long id,

        @Schema(description = "Field's name", example = "Cancha San justo")
        String name,

        @Schema(description = "Grass type", example = "Natural Grass")
        GrassType grassType,

        @Schema(description = "If the field is illuminated", example = "True")
        Boolean illuminated,

        @Schema(description = "Field's Location", example = "San justo 3054")
        Location location,

        @Schema(description = "Field's Images")
        List<Long> imageIds
) {
    public FieldDTO(Field field) {
            this(
                    field.getId(),
                    field.getName(),
                    field.getGrassType(),
                    field.isIlluminated(),
                    field.getLocation(),
                    field.getImages().stream().map(Image::getId).toList()
            );
        }
        public String getName(){
            return name;
        }
}

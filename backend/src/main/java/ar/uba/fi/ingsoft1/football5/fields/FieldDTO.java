package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.images.Image;

import java.util.List;

record FieldDTO(
        long id,
        String name,
        GrassType grassType,
        Boolean illuminated,
        Location location,
        List<Long> imageIds
) {
    FieldDTO(Field field) {
        this(
                field.getId(),
                field.getName(),
                field.getGrassType(),
                field.isIlluminated(),
                field.getLocation(),
                field.getImages().stream().map(Image::getId).toList()
        );
    }
}

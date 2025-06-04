package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.images.Image;

import java.util.List;
import java.util.Map;

public record FieldDTO(
        long id,
        String name,
        GrassType grassType,
        Boolean illuminated,
        Location location,
        List<Long> imageIds,
        Map<String, Integer> matchesWithMissingPlayers
) {
    public FieldDTO(Field field) {
            this(
                    field.getId(),
                    field.getName(),
                    field.getGrassType(),
                    field.isIlluminated(),
                    field.getLocation(),
                    field.getImages().stream().map(Image::getId).toList(),
                    null // No se solicitan los partidos abiertos
            );
    }

    public FieldDTO(Field field, Map<String, Integer> openMatches) {
        this(
                field.getId(),
                field.getName(),
                field.getGrassType(),
                field.isIlluminated(),
                field.getLocation(),
                field.getImages().stream().map(Image::getId).toList(),
                openMatches // Se incluyen los partidos abiertos con jugadores faltantes
        );
    }

}

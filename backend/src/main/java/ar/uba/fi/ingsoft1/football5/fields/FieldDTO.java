package ar.uba.fi.ingsoft1.football5.fields;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record FieldDTO(
        long id,
        String name,
        GrassType grassType,
        Boolean illuminated,
        Location location,
        Boolean enabled,
        List<String> imagesUrls,
        Map<LocalDateTime, Integer> matchesWithMissingPlayers
) {
    public FieldDTO(Field field) {
            this(
                    field.getId(),
                    field.getName(),
                    field.getGrassType(),
                    field.isIlluminated(),
                    field.getLocation(),
                    field.isEnabled(),
                    field.getImages().stream().map(image -> "/images/" + image.getId()).toList(),
                    null // Open Matches not requested
            );
    }

    public FieldDTO(Field field, Map<LocalDateTime, Integer> openMatches) {
        this(
                field.getId(),
                field.getName(),
                field.getGrassType(),
                field.isIlluminated(),
                field.getLocation(),
                field.isEnabled(),
                field.getImages().stream().map(image -> "/images/" + image.getId()).toList(),
                openMatches // Open matches requested, with missing players
        );
    }

}

package ar.uba.fi.ingsoft1.football5.fields;

record FieldDTO(
        long id,
        String name,
        GrassType grassType,
        Boolean illuminated,
        Location location
) {
    FieldDTO(Field field) {
        this(
                field.getId(),
                field.getName(),
                field.getGrassType(),
                field.isIlluminated(),
                field.getLocation()
        );
    }
}

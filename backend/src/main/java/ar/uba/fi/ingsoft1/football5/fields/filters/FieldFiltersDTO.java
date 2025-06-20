package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.GrassType;

public record FieldFiltersDTO(
        String name,
        String zone,
        String address,
        GrassType grassType,
        Boolean illuminated,
        Boolean hasOpenMatch,
        Boolean enabled
) {}

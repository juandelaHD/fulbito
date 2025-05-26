package ar.uba.fi.ingsoft1.football5.fields;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GrassType {
    NATURAL_GRASS,
    SYNTHETIC_TURF,
    HYBRID_TURF;

    @JsonCreator
    public static GrassType fromString(String value) {
        return GrassType.valueOf(value.toUpperCase());
    }
}

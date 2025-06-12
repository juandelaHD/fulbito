package ar.uba.fi.ingsoft1.football5.teams.formation;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum TeamFormationStrategyType {
    MANUAL,
    RANDOM,
    BY_AGE,
    BY_EXPERIENCE,
    BY_GENDER,
    BY_ZONE;

    public static final String ALL_VALUES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));
}
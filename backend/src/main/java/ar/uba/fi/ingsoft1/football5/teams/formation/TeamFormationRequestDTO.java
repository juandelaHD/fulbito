package ar.uba.fi.ingsoft1.football5.teams.formation;

import java.util.List;

public record TeamFormationRequestDTO(
        String strategy,
        List<Long> teamAPlayerIds,
        List<Long> teamBPlayerIds
) {}
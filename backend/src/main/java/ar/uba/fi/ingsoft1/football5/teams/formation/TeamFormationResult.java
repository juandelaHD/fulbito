package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.teams.Team;

public record TeamFormationResult(Team teamA, Team teamB) {
    public TeamFormationResult {
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("Teams cannot be null");
        }
        if (teamA.getMembers().isEmpty() || teamB.getMembers().isEmpty()) {
            throw new IllegalArgumentException("Teams must have players");
        }
    }
}
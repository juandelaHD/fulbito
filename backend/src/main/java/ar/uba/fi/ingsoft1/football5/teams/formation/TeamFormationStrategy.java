package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import java.util.Set;

public interface TeamFormationStrategy {
    static TeamFormationStrategy getStrategy(TeamFormationStrategyType strategy, Set<User> teamA, Set<User> teamB) {
        return switch (strategy) {
            case MANUAL -> new ManualTeamFormationStrategy(teamA, teamB);
            case RANDOM -> new RandomTeamFormationStrategy();
            case BY_AGE -> new AgeBalancedTeamFormationStrategy();
            case BY_EXPERIENCE -> new ExperienceBalancedTeamFormationStrategy();
            case BY_GENDER -> new GenderBalancedTeamFormationStrategy();
            case BY_ZONE -> new ZoneBalancedTeamFormationStrategy();
        };
    }
    TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) throws IllegalArgumentException;
}
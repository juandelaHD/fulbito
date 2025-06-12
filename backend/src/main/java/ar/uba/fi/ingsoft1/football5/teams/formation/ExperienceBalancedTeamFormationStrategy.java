package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import java.util.*;

public class ExperienceBalancedTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        List<User> sorted = TeamFormationUtils.getSortedPlayers(players, Comparator.comparingInt(User::getMatchesPlayed).reversed());
        List<User> teamAPlayers = new ArrayList<>();
        List<User> teamBPlayers = new ArrayList<>();
        int expA = 0, expB = 0;
        for (User user : sorted) {
            if (expA <= expB) {
                expA += user.getMatchesPlayed();
                teamAPlayers.add(user);
            } else {
                expB += user.getMatchesPlayed();
                teamBPlayers.add(user);
            }
        }
        return TeamFormationUtils.buildTeams(teamAPlayers, teamBPlayers, matchId);
    }
}
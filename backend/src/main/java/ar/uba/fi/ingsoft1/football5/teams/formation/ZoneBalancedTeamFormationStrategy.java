package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import java.util.*;

public class ZoneBalancedTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        Map<String, List<User>> byZone = new HashMap<>();
        for (User user : players) {
            byZone.computeIfAbsent(user.getZone(), z -> new ArrayList<>()).add(user);
        }
        List<User> teamAPlayers = new ArrayList<>();
        List<User> teamBPlayers = new ArrayList<>();
        for (List<User> group : byZone.values()) {
            TeamFormationUtils.distributeAlternately(group, teamAPlayers, teamBPlayers);
        }
        return TeamFormationUtils.buildTeams(teamAPlayers, teamBPlayers, matchId);
    }
}
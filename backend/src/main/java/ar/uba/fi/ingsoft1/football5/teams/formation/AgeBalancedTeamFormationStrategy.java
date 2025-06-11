package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import java.util.*;

public class AgeBalancedTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        List<User> sorted = TeamFormationUtils.getSortedPlayers(players, Comparator.comparingInt(User::getAge));
        List<User> teamAPlayers = new ArrayList<>();
        List<User> teamBPlayers = new ArrayList<>();
        int ageA = 0, ageB = 0;
        for (User user : sorted) {
            if (ageA <= ageB) {
                teamAPlayers.add(user);
                ageA += user.getAge();
            } else {
                teamBPlayers.add(user);
                ageB += user.getAge();
            }
        }
        // elegir un capitan al azar para cada equipo
        return TeamFormationUtils.buildTeams(teamAPlayers, teamBPlayers, matchId);
    }
}
package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import java.util.*;

public class GenderBalancedTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        List<User> allPlayers = new ArrayList<>(players);
        allPlayers.sort(Comparator.comparing(User::getGender));
        List<User> teamAPlayers = new ArrayList<>();
        List<User> teamBPlayers = new ArrayList<>();
        for (int i = 0; i < allPlayers.size(); i++) {
            if (i % 2 == 0) teamAPlayers.add(allPlayers.get(i));
            else teamBPlayers.add(allPlayers.get(i));
        }
        return TeamFormationUtils.buildTeams(teamAPlayers, teamBPlayers, matchId);
    }
}
package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import java.util.*;

public class RandomTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        List<User> playerList = new ArrayList<>(players);
        Collections.shuffle(playerList);

        List<User> teamAPlayers = new ArrayList<>();
        List<User> teamBPlayers = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++) {
            if (i % 2 == 0) teamAPlayers.add(playerList.get(i));
            else teamBPlayers.add(playerList.get(i));
        }
        return TeamFormationUtils.buildTeams(teamAPlayers, teamBPlayers, matchId);
    }
}
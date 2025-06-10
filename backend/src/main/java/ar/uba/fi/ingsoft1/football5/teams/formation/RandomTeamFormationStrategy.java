package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import java.util.*;

public class RandomTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        List<User> playerList = new ArrayList<>(players);
        Collections.shuffle(playerList);

        Team teamA = new Team();
        teamA.setName("Team A");
        Team teamB = new Team();
        teamB.setName("Team B");

        for (int i = 0; i < playerList.size(); i++) {
            if (i % 2 == 0) {
                teamA.getMembers().add(playerList.get(i));
            } else {
                teamB.getMembers().add(playerList.get(i));
            }
        }
        return new TeamFormationResult(teamA, teamB);
    }
}
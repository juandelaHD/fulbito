package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import java.util.*;

public class GenderBalancedTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        Map<String, List<User>> byGender = new HashMap<>();
        for (User user : players) {
            byGender.computeIfAbsent(user.getGender(), g -> new ArrayList<>()).add(user);
        }

        String teamAName = "Team A - Match " + matchId;
        String teamBName = "Team B - Match " + matchId;

        Team teamA = new Team();
        teamA.setName(teamAName);
        Team teamB = new Team();
        teamB.setName(teamBName);

        boolean toggle = true;
        for (List<User> group : byGender.values()) {
            for (User user : group) {
                if (toggle) teamA.addMember(user);
                else teamB.addMember(user);
                toggle = !toggle;
            }
        }
        return new TeamFormationResult(teamA, teamB);
    }
}
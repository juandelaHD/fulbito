package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import java.util.*;

public class AgeBalancedTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        List<User> sorted = new ArrayList<>(players);
        sorted.sort(Comparator.comparingInt(User::getAge).reversed());

        String teamAName = "Team A - Match " + matchId;
        String teamBName = "Team B - Match " + matchId;

        Team teamA = new Team();
        teamA.setName(teamAName);
        Team teamB = new Team();
        teamB.setName(teamBName);

        int sumA = 0, sumB = 0;
        for (User user : sorted) {
            if (sumA <= sumB) {
                teamA.addMember(user);
                sumA += user.getAge();
            } else {
                teamB.addMember(user);
                sumB += user.getAge();
            }
        }
        return new TeamFormationResult(teamA, teamB);
    }
}
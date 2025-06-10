package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import java.util.*;

public class ExperienceBalancedTeamFormationStrategy implements TeamFormationStrategy {
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        List<User> sorted = new ArrayList<>(players);
        sorted.sort(Comparator.comparingInt(User::getMatchesPlayed).reversed());

        String teamAName = "Team A - Match " + matchId;
        String teamBName = "Team B - Match " + matchId;

        Team teamA = new Team();
        teamA.setName(teamAName);
        Team teamB = new Team();
        teamB.setName(teamBName);

        int expA = 0, expB = 0;
        for (User user : sorted) {
            if (expA <= expB) {
                teamA.addMember(user);
                expA += user.getMatchesPlayed();
            } else {
                teamB.addMember(user);
                expB += user.getMatchesPlayed();
            }
        }
        return new TeamFormationResult(teamA, teamB);
    }
}
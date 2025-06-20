package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.user.User;

import java.util.*;

public class TeamFormationUtils {
    public static TeamFormationResult buildTeams(
            List<User> teamAPlayers, List<User> teamBPlayers, Long matchId) {
        String uniqueSuffix = "-" + System.currentTimeMillis();
        String teamAName = "Team A - Match " + matchId + " - " + uniqueSuffix;
        String teamBName = "Team B - Match " + matchId + " - " + uniqueSuffix;
        Random random = new Random();
        User captainA = teamAPlayers.get(random.nextInt(teamAPlayers.size()));
        User captainB = teamBPlayers.get(random.nextInt(teamBPlayers.size()));

        Team teamA = new Team(teamAName, captainA);
        teamAPlayers.forEach(teamA::addMember);

        Team teamB = new Team(teamBName, captainB);
        teamBPlayers.forEach(teamB::addMember);

        if (teamA.getMembers().size() != teamB.getMembers().size()) {
            throw new IllegalArgumentException("Teams must have the same number of members.");
        }

        teamA.setMainColor("#8B0000");
        teamA.setSecondaryColor("#FF69B4");

        teamB.setMainColor("#4B0082");
        teamB.setSecondaryColor("#20B2AA");

        teamA.setRanking(100); // Example ranking, can be adjusted
        teamB.setRanking(100); // Example ranking, can be adjusted

        return new TeamFormationResult(teamA, teamB);
    }

    public static List<User> getSortedPlayers(Set<User> players, Comparator<User> comparator) {
        List<User> sorted = new ArrayList<>(players);
        sorted.sort(comparator);
        return sorted;
    }

    public static void distributeAlternately(Collection<? extends User> users, List<User> teamA, List<User> teamB) {
        boolean toggle = true;
        for (User user : users) {
            if (toggle) teamA.add(user);
            else teamB.add(user);
            toggle = !toggle;
        }
    }
}
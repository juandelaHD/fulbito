package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.teams.Team;

import java.util.HashSet;
import java.util.Set;

public class ManualTeamFormationStrategy implements TeamFormationStrategy {
    private final Set<User> teamAMembers;
    private final Set<User> teamBMembers;

    public ManualTeamFormationStrategy(Set<User> teamAMembers, Set<User> teamBMembers) {
        this.teamAMembers = teamAMembers;
        this.teamBMembers = teamBMembers;
    }

    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) {
        if (teamAMembers.size() != teamBMembers.size()) {
            throw new IllegalArgumentException("Both teams must have the same number of members.");
        }

        Set<User> allPlayers = new HashSet<>(teamAMembers);
        allPlayers.addAll(teamBMembers);
        if (allPlayers.size() != teamAMembers.size() + teamBMembers.size()) {
            throw new IllegalArgumentException("Cannot have duplicate players in teams.");
        }

        String teamAName = "Team A - Match " + matchId;
        String teamBName = "Team B - Match " + matchId;

        Team teamA = new Team();
        teamA.setName(teamAName);
        teamA.setMembers(teamAMembers);

        Team teamB = new Team();
        teamB.setName(teamBName);
        teamB.setMembers(teamBMembers);

        return new TeamFormationResult(teamA, teamB);
    }
}
package ar.uba.fi.ingsoft1.football5.teams;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamService {

    private static final String TEAM_ITEM = "team";
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final ImageService imageService;

    public TeamService(TeamRepository teamRepository, UserService userService, ImageService imageService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.imageService = imageService;
    }

    public TeamDTO createTeam(TeamCreateDTO teamCreate, String username, MultipartFile image) throws IllegalArgumentException{
        validateUniqueName(teamCreate);

        User captain = userService.loadUserByUsername(username);
        Team team = new Team(teamCreate.name(), captain);

        team.setMainColor(teamCreate.mainColor() != null ? teamCreate.mainColor() : "#FFFFFF");
        team.setSecondaryColor(teamCreate.secondaryColor() != null ? teamCreate.secondaryColor() : "#000000");
        team.setRanking(teamCreate.ranking() != null ? teamCreate.ranking() : 100);
        Team teamSaved = teamRepository.save(team);

        try {
            imageService.saveTeamImage(teamSaved, image);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving team image: " + e.getMessage());
        }

        return new TeamDTO(teamSaved);
    }

    private void validateUniqueName(TeamCreateDTO teamCreate) {
        if (teamRepository.existsByName(teamCreate.name().toLowerCase())) {
            throw new IllegalArgumentException("The name of the team already exists");
        }
    }

    public List<TeamDTO> getTeamsByCaptain(String username) throws UserNotFoundException {
        User captain = userService.loadUserByUsername(username);
        return teamRepository.findByCaptainId(captain.getId()).stream().map(TeamDTO::new).toList();
    }

    public List<TeamDTO> getAllTeams(){
        return teamRepository.findAll().stream().map(TeamDTO::new).toList();
    }

    public TeamDTO getTeamById(Long id) throws ItemNotFoundException,IllegalArgumentException {
        Team team = teamRepository.findById(id) 
            .orElseThrow(() -> new ItemNotFoundException("team", id));
        return new TeamDTO(team);
    }

    public TeamDTO addMember(Long teamId, String usernameToAdd, String captainUsername) throws UserNotFoundException, ItemNotFoundException, IllegalArgumentException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ItemNotFoundException(TEAM_ITEM, teamId));

        validateIsCaptain(team, captainUsername);

        User userToAdd = userService.loadUserByUsername(usernameToAdd);
        validateIsNotAlreadyMember(team, userToAdd);

        team.addMember(userToAdd);
        return new TeamDTO(teamRepository.save(team));
    }

    private void validateIsNotAlreadyMember(Team team, User userToAdd) {
        if (team.getMembers().contains(userToAdd)) {
            throw new IllegalArgumentException("The user is already a member of the team.");
        }
    }

    private void validateIsCaptain(Team team, String captainUsername) {
        if (!team.getCaptain().getUsername().equalsIgnoreCase(captainUsername)) {
            throw new IllegalArgumentException("Only the captain can edit the team members.");
        }
    }

    private void validateIsAlreadyMember(Team team, User userToRemove) {
        if (!team.getMembers().contains(userToRemove)) {
            throw new IllegalArgumentException("The user is not a member of the team.");
        }
    }

    public TeamDTO removeMember(Long teamId, String usernameToRemove, String captainUsername)
            throws UserNotFoundException, ItemNotFoundException, IllegalArgumentException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ItemNotFoundException(TEAM_ITEM, teamId));

        validateIsCaptain(team, captainUsername);

        User userToRemove = userService.loadUserByUsername(usernameToRemove);
        validateIsAlreadyMember(team, userToRemove);

        team.removeMember(userToRemove);
        return new TeamDTO(teamRepository.save(team));
    }

    public TeamDTO updateTeam(Long id, TeamCreateDTO teamCreate, String username, MultipartFile image)
            throws ItemNotFoundException, IllegalArgumentException, IOException {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(TEAM_ITEM, id));

        validateIsCaptain(team, username);
        if (!teamCreate.name().equalsIgnoreCase(team.getName())){
            validateUniqueName(teamCreate);
        }
        team.setName(teamCreate.name());

        if (teamCreate.mainColor() != null) {
            team.setMainColor(teamCreate.mainColor());
        }
        if (teamCreate.secondaryColor() != null) {
            team.setSecondaryColor(teamCreate.secondaryColor());
        }
        if (teamCreate.ranking() != null) {
            team.setRanking(teamCreate.ranking());
        }

        TeamDTO updatedTeam = new TeamDTO(teamRepository.save(team));

        if (image != null && !image.isEmpty()) {
            Team updated = teamRepository.findById(updatedTeam.id()).orElseThrow();
            imageService.saveTeamImage(updated, image);
        }

        return updatedTeam;
    }

    public void deleteTeam(Long id, String username) throws ItemNotFoundException, IllegalArgumentException {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(TEAM_ITEM, id));

        validateIsCaptain(team, username);
        teamRepository.delete(team);
    }

    public void uploadTeamImage(Long teamId, MultipartFile file, String username)
            throws ItemNotFoundException, IOException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ItemNotFoundException(TEAM_ITEM, teamId));

        validateIsCaptain(team, username);
        imageService.saveTeamImage(team, file);
    }
}
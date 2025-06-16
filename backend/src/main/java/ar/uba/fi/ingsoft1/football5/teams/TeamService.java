package ar.uba.fi.ingsoft1.football5.teams;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final ImageService imageService;

    public TeamService(TeamRepository teamRepository, UserService userService, ImageService imageService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.imageService = imageService;
    }

    public Optional<TeamDTO> createTeam(TeamCreateDTO dto, String username, MultipartFile image) throws IllegalArgumentException {
        if (teamRepository.existsByName(dto.name().toLowerCase())) {
            throw new IllegalArgumentException("The name of the team already exists");
        }
        User captain = userService.loadUserByUsername(username);
        Team team = new Team(dto.name().toLowerCase(), captain);
        // Set default colors if not provided
        team.setMainColor(dto.mainColor() != null ? dto.mainColor() : "#FFFFFF");
        team.setSecondaryColor(dto.secondaryColor() != null ? dto.secondaryColor() : "#000000");
        team.setRanking(dto.ranking() != null ? dto.ranking() : 100);
        Team teamSaved = teamRepository.save(team);
        try {
            imageService.saveTeamImage(teamSaved, image);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving team image: " + e.getMessage());
        }
        return Optional.of(new TeamDTO(teamSaved));
    }

    public List<TeamDTO> getTeamsByCaptain(String username) throws UserNotFoundException {
        User captain = userService.loadUserByUsername(username);
        return teamRepository.findByCaptainId(captain.getId()).stream().map(TeamDTO::new).toList();
    }

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream().map(TeamDTO::new).toList();
    }

    public TeamDTO addMember(Long teamId, String usernameToAdd, String captainUsername) throws UserNotFoundException, ItemNotFoundException, IllegalArgumentException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ItemNotFoundException("team", teamId));
        if (!team.getCaptain().getUsername().equalsIgnoreCase(captainUsername)) {
            throw new IllegalArgumentException("Only the captain can add members.");
        }
        User userToAdd = userService.loadUserByUsername(usernameToAdd);
        if (team.getMembers().contains(userToAdd)) {
            throw new IllegalArgumentException("The user is already a member of the team.");
        }
        team.addMember(userToAdd);
        return new TeamDTO(teamRepository.save(team));
    }
 
    public TeamDTO removeMember(Long teamId, String usernameToRemove, String captainUsername) throws UserNotFoundException, ItemNotFoundException, IllegalArgumentException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ItemNotFoundException("team", teamId));
        if (!team.getCaptain().getUsername().equalsIgnoreCase(captainUsername)) {
            throw new IllegalArgumentException("Only the captain can remove members.");
        }
        User userToRemove = userService.loadUserByUsername(usernameToRemove);
        if (!team.getMembers().contains(userToRemove)) {
            throw new IllegalArgumentException("The user is not a member of the team.");
        }
        team.removeMember(userToRemove);
        return new TeamDTO(teamRepository.save(team));
    }

    public TeamDTO updateTeam(Long id, TeamCreateDTO dto, String username) throws ItemNotFoundException, IllegalArgumentException {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("team", id));
        if (!team.getCaptain().getUsername().equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("You are not the captain of this team.");
        }
        if (dto.name() != null && !dto.name().equals(team.getName())) {
            if (teamRepository.existsByName(dto.name())) {
                throw new IllegalArgumentException("The name of the team already exists");
            }
            team.setName(dto.name());
        }
        if (dto.mainColor() != null) {
            team.setMainColor(dto.mainColor());
        }
        if (dto.secondaryColor() != null) {
            team.setSecondaryColor(dto.secondaryColor());
        }
        if (dto.ranking() != null) {
            team.setRanking(dto.ranking());
        }
        return new TeamDTO(teamRepository.save(team));
    }

    public void deleteTeam(Long id, String username) throws ItemNotFoundException, IllegalArgumentException {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("team", id));
        if (!team.getCaptain().getUsername().equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("You are not the captain of this team.");
        }
        teamRepository.delete(team);
    }
}
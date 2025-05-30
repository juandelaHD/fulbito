package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.fields.FieldDTO;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    //para testear
    //TODO: deberia mejorar los test en vees de tener esto

    public MatchService(MatchRepository matchRepo, UserRepository userRepo) {
        this.matchRepo = matchRepo;
        this.userRepo = userRepo;
    }

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private UserRepository userRepo;

    public List<MatchSummaryDTO> getAvailableMatches() {
        return matchRepo.findBycloseMatchFalse().stream()
            .filter(match -> !match.started() && !match.isFull())
            .map(match -> new MatchSummaryDTO(
                match.getId(),
                new FieldDTO(match.getField()),
                match.getDate(),
                match.getHour(),
                match.getMissingPlayers()))
            .collect(Collectors.toList());
    }

    public InscripcionResponse register(Long matchId, Long userId) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (match.started()) throw new RuntimeException("The game has already started");
        if (match.isFull()) throw new RuntimeException("The match is full");
        if (match.getPlayers().contains(user)) throw new RuntimeException("You are already registered");

        match.getPlayers().add(user);
        matchRepo.save(match);

        return new InscripcionResponse("Successful registration", match);
    }
//TODO: Mejorar el trato de los errores
}

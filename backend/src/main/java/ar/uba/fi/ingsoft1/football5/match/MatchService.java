package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    //para testear
    public MatchService(MatchRepository matchRepo, UserRepository userRepo) {
        this.matchRepo = matchRepo;
        this.userRepo = userRepo;
    }

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private UserRepository userRepo;

    public List<MatchSummaryDTO> getAvailableMatches() {
        return matchRepo.findByCloseFalse().stream()
            .filter(match -> !match.started() && !match.isFull())
            .map(match -> new MatchSummaryDTO(
                match.getId(),
                match.getField(),
                match.getDate(),
                match.getHour(),
                match.getMissingPlayers()))
            .collect(Collectors.toList());
    }

    public InscripcionResponse register(Long matchId, Long userId) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (match.started()) throw new RuntimeException("El partido ya comenzó");
        if (match.isFull()) throw new RuntimeException("El partido ya está lleno");
        if (match.getPlayers().contains(user)) throw new RuntimeException("Ya estás inscrito");

        match.getPlayers().add(user);
        matchRepo.save(match);

        return new InscripcionResponse("Inscripción exitosa", match);
    }

}

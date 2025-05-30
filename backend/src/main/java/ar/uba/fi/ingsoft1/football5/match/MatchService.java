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

    public List<MatchSummaryDTO> obtenerPartidosDisponibles() {
        return matchRepo.findByCerradoFalse().stream()
            .filter(match -> !match.yaEmpezo() && !match.estaLleno())
            .map(match -> new MatchSummaryDTO(
                match.getId(),
                match.getCancha().getName(),
                match.getFecha(),
                match.getHora(),
                match.getJugadoresFaltantes()))
            .collect(Collectors.toList());
    }

    public InscripcionResponse inscribirse(Long matchId, Long userId) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (match.yaEmpezo()) throw new RuntimeException("El partido ya comenzó");
        if (match.estaLleno()) throw new RuntimeException("El partido ya está lleno");
        if (match.getJugadores().contains(user)) throw new RuntimeException("Ya estás inscrito");

        match.getJugadores().add(user);
        matchRepo.save(match);

        return new InscripcionResponse("Inscripción exitosa", match);
    }

}

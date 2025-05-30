package ar.uba.fi.ingsoft1.football5.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/disponibles")
    public List<MatchSummaryDTO> listarPartidosDisponibles() {
        return matchService.obtenerPartidosDisponibles();
    }

    @PostMapping("/{id}/inscribirse")
    public ResponseEntity<?> inscribirse(
            @PathVariable Long id,
            @RequestBody InscripcionRequest request) {
        try {
            InscripcionResponse respuesta = matchService.inscribirse(id, request.getUserId());
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

package ar.uba.fi.ingsoft1.football5.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/available")
    public List<MatchSummaryDTO> listAvailableMatches() {
        return matchService.getAvailableMatches();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> register(
            @PathVariable Long id,
            @RequestBody InscripcionRequest request) {
        try {
            InscripcionResponse answer = matchService.register(id, request.getUserId());
            return ResponseEntity.ok(answer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

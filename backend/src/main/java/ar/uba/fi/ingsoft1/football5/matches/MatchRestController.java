package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
@Tag(name = "3 - Matches", description = "Endpoints for managing football matches")
public class MatchRestController {
    private final MatchService matchService;

    @Autowired
    MatchRestController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping(path = "/{matchId}", produces = "application/json")
    @Operation(summary = "Get match details by ID")
    @ResponseStatus(HttpStatus.OK)
    MatchDTO getMatch(@NonNull @PathVariable Long matchId) throws ItemNotFoundException {
        return matchService.getMatchById(matchId);
    }

    @PostMapping(path = "/create-open", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Create a new match")
    @ResponseStatus(HttpStatus.CREATED)
    MatchDTO createMatch(@NonNull @RequestBody MatchCreateDTO matchCreate) throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {
        {
            return matchService.createOpenMatch(matchCreate);
        }
    }
}




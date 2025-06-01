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

    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Create a new match")
    @ResponseStatus(HttpStatus.CREATED)
    MatchDTO createMatch(@NonNull @RequestBody MatchCreateDTO matchCreate) throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {
        {
            if (matchCreate.matchType() == MatchType.OPEN) {
                // Validate that the match type is OPEN
                return matchService.createOpenMatch(matchCreate);
            } else if (matchCreate.matchType() == MatchType.CLOSED) {
                // Handle private match creation logic here
                // For now, it thows an exception if the match type is CLOSED
                throw new IllegalArgumentException("Match type CLOSED is not supported yet.");
                // return matchService.createClosedMatch(matchCreate);
            }
            return matchService.createOpenMatch(matchCreate);
        }
    }

    @PostMapping("/{matchId}/join")
    @Operation(summary = "Join an open match")
    @ResponseStatus(HttpStatus.OK)
    public MatchDTO joinOpenMatch(@PathVariable Long matchId, @RequestParam Long userId) throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {
        return matchService.joinOpenMatch(matchId, userId);
    }

}




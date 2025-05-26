package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UnauthorizedException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import static java.lang.String.format;

@RestController
@RequestMapping("/users")
@Tag(name = "2 - Users")
class UserRestController {

    private static final String NOT_FOUND_TEMPLATE = "Could not find user with username %s";

    private final UserService userService;

    @Autowired
    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{username}", produces = "application/json")
    @Operation(summary = "Get user profile")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(hasRole('USER'))")
    ResponseEntity<UserDTO> getUserByUsername(@NonNull @PathVariable String username) {
        try {
            UserDTO user = userService.getUserByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (UserNotFoundException ex) {
            String messages = format(NOT_FOUND_TEMPLATE, username);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, messages);
        }
    }



}

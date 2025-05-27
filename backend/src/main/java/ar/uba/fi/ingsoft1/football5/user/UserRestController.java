package ar.uba.fi.ingsoft1.football5.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "2 - Users")
class UserRestController {

    private final UserService userService;

    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{username}", produces = "application/json")
    @Operation(summary = "Get user profile")
    @ResponseStatus(HttpStatus.OK)
    // TODO: When are we using this service? Is it for the user or admin profile page? Decide PreAuthorize annotation.
    UserDTO getUser(@NonNull @PathVariable String username) {
        return userService.getUser(username);
    }
}

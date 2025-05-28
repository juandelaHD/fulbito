package ar.uba.fi.ingsoft1.football5.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/sessions")
@Tag(name = "1 - Sessions")
class SessionRestController {

    private final UserService userService;

    @Autowired
    SessionRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/sign-up", produces = "application/json")
    @Operation(
            summary = "Create a new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "400",
                            description = "User creation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "User creation failed",
                          "path": "/sessions/sign-up"
                        }
                        """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<TokenDTO> signUp(
            @Valid @NonNull @RequestBody UserCreateDTO data) {
        return userService
                .createUser(data)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User creation failed"));
    }

    @PostMapping(path = "/login", produces = "application/json")
    @Operation(
            summary = "Log in, creating a new session",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful login",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid username or password supplied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                        {
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Username or password is incorrect",
                          "path": "/sessions/login"
                        }
                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Email is not confirmed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                        {
                          "status": 403,
                          "error": "Forbidden",
                          "message": "Email is not confirmed",
                          "path": "/sessions/login"
                        }
                        """
                                    )
                            )
                    )
            }
    )
    public TokenDTO login(
            @Valid @NonNull @RequestBody UserLoginDTO data
    ) {
        return userService
                .loginUser(data)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect"));
    }

    @PutMapping(path = "/refresh", produces = "application/json")
    @Operation(
            summary = "Refresh a session",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Session successfully refreshed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenDTO.class)
                            )),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid refresh token supplied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                        {
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Invalid refresh token supplied",
                          "path": "/sessions/refresh"
                        }
                        """
                                    )
                            )
                    )
            }
    )
    public TokenDTO refresh(
            @Valid @NonNull @RequestBody RefreshDTO data
    ) {
        return userService
                .refresh(data)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token supplied"));
    }

    @GetMapping("/verify-email")
    @Operation(
            summary = "Verify user email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email verified successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                        {
                          "message": "Email verified successfully!"
                        }
                        """
                                    )
                            )),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid verification token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Invalid verification token",
                          "path": "/sessions/verify-email"
                        }
                        """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        return userService.verifyEmail(token)
                .map(user -> ResponseEntity.ok(Map.of("message", "Email verified successfully!")))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token"));
    }
}

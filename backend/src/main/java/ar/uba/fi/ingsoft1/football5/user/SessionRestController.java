package ar.uba.fi.ingsoft1.football5.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/sessions")
@Tag(name = "1 - Sessions")
class SessionRestController {

    private final UserService userService;

    SessionRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/sign-up", produces = "application/json")
    @Operation(summary = "Create a new user")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid user data supplied", content = @Content)
    ResponseEntity<TokenDTO> signUp(
            @RequestParam("user")
            @Parameter(
                    description = "UserCreateDTO JSON payload",
                    schema = @Schema(type = "string", format = "json", implementation = UserCreateDTO.class)
            ) String userJson,
            @RequestPart(value = "avatar") MultipartFile avatar) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserCreateDTO data = objectMapper.convertValue(userJson, UserCreateDTO.class);

        return userService
            .createUser(data, avatar)
            .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User creation failed"));
    }

    @PostMapping(path = "/login", produces = "application/json")
    @Operation(summary = "Log in, creating a new session")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Successful login", content = @Content)
    @ApiResponse(responseCode = "401", description = "Invalid username or password supplied", content = @Content)
    public TokenDTO login(
            @Valid @NonNull @RequestBody UserLoginDTO data
    ) {
        return userService
                .loginUser(data)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password supplied"));
    }

    @PutMapping(path = "/refresh", produces = "application/json")
    @Operation(summary = "Refresh a session")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "401", description = "Invalid refresh token supplied", content = @Content)
    public TokenDTO refresh(
            @Valid @NonNull @RequestBody RefreshDTO data
    ) {
        return userService
                .refresh(data)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}

package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.user.password_reset_token.ForgotPasswordRequestDTO;
import ar.uba.fi.ingsoft1.football5.user.password_reset_token.ResetPasswordDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/sessions")
@Tag(name = "1 - Sessions")
class SessionRestController {

    private final UserService userService;

    @Autowired
    private Validator validator;

    SessionRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/sign-up", produces = "application/json", consumes = "multipart/form-data")
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
            @RequestParam("user")
            @Parameter(
                    description = "UserCreateDTO JSON payload",
                    schema = @Schema(type = "string", format = "json", implementation = UserCreateDTO.class)
            ) String userJson,
            MultipartFile avatar) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserCreateDTO data = objectMapper.readValue(userJson, UserCreateDTO.class);
        // Validate JSON data with validator annotations in UserCreateDTO
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<UserCreateDTO> violation : violations) {
                errorMessage.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }
        return userService
                .createUser(data, avatar)
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

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Sends an email with a link to reset the password if the user exists.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ForgotPasswordRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Request received. If the email exists, a reset link is sent."
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public void forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        userService.initiatePasswordReset(request.email());
    }

    @GetMapping("/reset-password")
    @Operation(
            summary = "Redirect to frontend for password reset",
            description = "Redirects to the frontend application with the reset token.",
            parameters = {
                    @Parameter(name = "token", description = "Password reset token", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirects to the frontend application"
                    )
            }
    )
    public void redirectToFrontend(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        String frontendUrl = "http://localhost:30003/reset-password?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(frontendUrl);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password using token",
            description = "Allows setting a new password using the token received by email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password successfully reset."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or expired token, or passwords do not match.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                    {
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Invalid or expired token",
                      "path": "/sessions/reset-password"
                    }
                    """
                                    )
                            )
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(dto.token(), dto.newPassword(), dto.confirmPassword());
    }
}


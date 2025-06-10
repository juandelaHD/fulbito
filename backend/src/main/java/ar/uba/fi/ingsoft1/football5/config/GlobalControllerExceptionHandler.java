package ar.uba.fi.ingsoft1.football5.config;

import ar.uba.fi.ingsoft1.football5.common.exception.ErrorResponse;
import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UnauthorizedException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid arguments supplied",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 400, \"error\": \"Bad Request\", \"message\": {\"username\": \"Username is required\"}, \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleMethodArgumentInvalid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errors,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ApiResponse(
            responseCode = "404",
            description = "Referenced entity not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 404, \"error\": \"Not Found\", \"message\": \"Failed to find item with id 42\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleItemNotFound(ItemNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid JWT access token supplied",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Access denied\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized access",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Unauthorized access to resource\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 404, \"error\": \"Not Found\", \"message\": \"Failed to find user with username john_doe\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid username or password supplied",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Invalid username or password supplied\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(
            responseCode = "406",
            description = "Invalid arguments supplied",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 406, \"error\": \"Not Acceptable\", \"message\": \"Invalid argument: x must be greater than 0\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_ACCEPTABLE.value(),
                "Not Acceptable",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid JSON format",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Invalid JSON format. Please check your request body syntax.\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid JSON format. Please check your request body syntax.",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(java.io.IOException.class)
    @ApiResponse(
            responseCode = "400",
            description = "Input/output error (e.g., file too large)",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Input/output error: <message>\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleIOException(java.io.IOException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Input/output error: " + ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            example = "{ \"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Unexpected error message\", \"path\": \"<actual_path_here>\" }"
                    )
            )
    )
    public ResponseEntity<ErrorResponse> handleFallback(Throwable ex, HttpServletRequest request) {
        logger.error("Unhandled exception", ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getClass().getCanonicalName() + ": " + ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/images")
@Tag(name = "6 - Images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/{id}", produces = "image/jpeg")
    @Operation(summary = "Get an image by its Id")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Image retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid image Id supplied", content = @Content)
    @ApiResponse(responseCode = "404", description = "Image not found", content = @Content)
    public ResponseEntity<byte[]> getImage(@Valid @PathVariable @Positive Long id
    ) {
        try {
            // Attempt to retrieve the image data
            byte[] imageData = imageService.getImageData(id);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (ItemNotFoundException e) {
            // If the image is not found, throw a 404 error
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found", e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an image by its Id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Image deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid image Id supplied", content = @Content)
    @ApiResponse(responseCode = "404", description = "Image not found", content = @Content)
    public void deleteImage(@AuthenticationPrincipal JwtUserDetails userDetails,
                            @Valid @PathVariable @Positive Long id) throws ItemNotFoundException {
        imageService.deleteImage(id, userDetails);
    }
}


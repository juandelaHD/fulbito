package ar.uba.fi.ingsoft1.football5.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/fields")
@Tag(name = "Fields")
class FieldRestController {

    private final FieldService fieldService;

    @Autowired
    FieldRestController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @PostMapping(produces = "application/json", consumes = "multipart/form-data")
    @Operation(summary = "Create a new field")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Field created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid field data supplied", content = @Content)

    // Currently, this endpoint is not secured. Uncomment the line
    // below to secure it when users authentication is implemented.
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    FieldDTO createField(
            @RequestParam("field")
            @Parameter(
                    description = "FieldCreateDTO JSON payload",
                    schema = @Schema(type = "string", format = "json", implementation = FieldCreateDTO.class)
            ) String fieldJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IllegalArgumentException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        FieldCreateDTO fieldCreate = objectMapper.readValue(fieldJson, FieldCreateDTO.class);
        return fieldService.createField(fieldCreate, images);
    }
}



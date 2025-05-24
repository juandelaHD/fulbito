package ar.uba.fi.ingsoft1.football5.fields;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fields")
@Tag(name = "Fields")
class FieldRestController {

    private final FieldService fieldService;

    @Autowired
    FieldRestController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Create a new field")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Field created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid field data supplied", content = @Content)
    // Currently, this endpoint is not secured. Uncomment the line
    // below to secure it when users authentication is implemented.
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    FieldDTO createField(
            @Valid @RequestBody FieldCreateDTO fieldCreate
    ) throws IllegalArgumentException {
        return fieldService.createField(fieldCreate);
    }
}

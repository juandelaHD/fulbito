package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.filters.FieldFiltersDTO;
import ar.uba.fi.ingsoft1.football5.fields.reviews.ReviewCreateDTO;
import ar.uba.fi.ingsoft1.football5.fields.reviews.ReviewDTO;
import ar.uba.fi.ingsoft1.football5.fields.reviews.ReviewService;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleCreateDTO;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleDTO;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleService;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleSlotDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fields")
@Tag(name = "4 - Fields")
class FieldRestController {

    private final FieldService fieldService;
    private final ReviewService reviewService;
    private final ScheduleService scheduleService;

    @Autowired
    FieldRestController(FieldService fieldService, ReviewService reviewService, ScheduleService scheduleService) {
        this.fieldService = fieldService;
        this.reviewService = reviewService;
        this.scheduleService = scheduleService;
    }

    @GetMapping(path = "/filters", produces = "application/json")
    @Operation(summary = "Get all fields with pagination and filters")
    @ApiResponse(responseCode = "200", description = "Fields retrieved successfully")
    Page<FieldDTO> getFields(
            @Valid @ParameterObject Pageable pageable,
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "zone", required = false) String zone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "grassType", required = false) GrassType grassType,
            @RequestParam(value = "isIlluminated", required = false) Boolean isIlluminated,
            @RequestParam(value = "hasOpenScheduledMatch", required = false) Boolean hasOpenScheduledMatch,
            @RequestParam(value = "isEnabled", required = false) Boolean isEnabled
    ) {
        FieldFiltersDTO filters = new FieldFiltersDTO(name, zone, address, grassType, isIlluminated, hasOpenScheduledMatch, isEnabled);
        return fieldService.getFieldsWithFilters(pageable, userDetails, filters);
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Get all fields with pagination")
    @ApiResponse(responseCode = "200", description = "Fields retrieved successfully")
    Page<FieldDTO> getAllFields(
            @Valid @ParameterObject Pageable pageable
    ) {
        return fieldService.getFieldsWithNonFilters(pageable);
    }

    @GetMapping(path = "/owned", produces = "application/json")
    @Operation(summary = "Get fields owned by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Owned fields retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    Page<FieldDTO> getOwnedFields(
            @Valid @ParameterObject Pageable pageable,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        return fieldService.getOwnedFields(pageable, userDetails);
    }

    @PostMapping(produces = "application/json", consumes = "multipart/form-data")
    @Operation(summary = "Create a new field")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Field created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid field data supplied", content = @Content)
    @PreAuthorize("hasRole('ADMIN')")
    FieldDTO createField(
            @RequestParam("field")
            @Parameter(
                    description = "FieldCreateDTO JSON payload",
                    schema = @Schema(type = "string", format = "json", implementation = FieldCreateDTO.class)
            ) String fieldJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws IllegalArgumentException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        FieldCreateDTO fieldCreate = objectMapper.readValue(fieldJson, FieldCreateDTO.class);
        return fieldService.createField(fieldCreate, images, userDetails);
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    @Operation(summary = "Delete a field by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Field deleted successfully")
    @ApiResponse(responseCode = "404", description = "Field not found", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid field ID supplied", content = @Content)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteField(
            @PathVariable("id") @Parameter(description = "ID of the field to delete") Long id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws ItemNotFoundException {
        fieldService.deleteField(id, userDetails);
    }

    @PutMapping(path = "/{id}", produces = "application/json", consumes = "multipart/form-data")
    @Operation(summary = "Update a field by ID")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Field updated successfully")
    @ApiResponse(responseCode = "404", description = "Field not found", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid field data supplied", content = @Content)
    @PreAuthorize("hasRole('ADMIN')")
    FieldDTO updateField(
            @PathVariable("id") @Parameter(description = "ID of the field to update") Long id,
            @RequestParam("field")
            @Parameter(
                    description = "FieldCreateDTO JSON payload",
                    schema = @Schema(type = "string", format = "json", implementation = FieldCreateDTO.class)
            ) String fieldJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws ItemNotFoundException, IllegalArgumentException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FieldCreateDTO fieldCreate = objectMapper.readValue(fieldJson, FieldCreateDTO.class);
        return fieldService.updateField(id, fieldCreate, images, userDetails);
    }

    // --- Reviews Endpoints

    @GetMapping(path = "/{id}/reviews", produces = "application/json")
    @Operation(summary = "Get reviews for a field by ID")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Field not found", content = @Content)
    Page<ReviewDTO> getReviewsByFieldId(
            @Valid @ParameterObject Pageable pageable,
            @PathVariable("id") @Parameter(description = "ID of the field") Long fieldId
    ) throws ItemNotFoundException {
        return reviewService.getReviewsByFieldId(fieldId, pageable);
    }

    @PostMapping(path = "/{id}/reviews", produces = "application/json")
    @Operation(summary = "Create a review for a field by ID")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Review created successfully")
    @ApiResponse(responseCode = "404", description = "Field not found", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid review data supplied", content = @Content)
    @PreAuthorize("hasRole('USER')")
    ReviewDTO createReview(
            @PathVariable("id") @Parameter(description = "ID of the field") Long fieldId,
            @Valid @RequestBody ReviewCreateDTO reviewCreateDTO,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws ItemNotFoundException {
        return reviewService.createReview(reviewCreateDTO, fieldId, userDetails);
    }

    // --- Schedules endpoints

    @PostMapping(path = "/{id}/schedules", produces = "application/json")
    @Operation(summary = "Create a schedule for a field")
    @ApiResponse(responseCode = "201", description = "Schedule created successfully")
    @ApiResponse(responseCode = "404", description = "Field not found", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid schedule data supplied", content = @Content)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    List<ScheduleDTO> createSchedule(
            @PathVariable("id") @Parameter(description = "ID of the field to schedule") Long fieldId,
            @Valid @RequestBody ScheduleCreateDTO scheduleCreate,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws ItemNotFoundException, IllegalArgumentException {
        return scheduleService.createSchedule(fieldId, scheduleCreate, userDetails);
    }

    @GetMapping(path = "/{id}/schedules", produces = "application/json")
    @Operation(summary = "Get schedules for a field by ID")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Schedules retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Field not found", content = @Content)
    Page<ScheduleDTO> getSchedulesByFieldId(
            @Valid @ParameterObject Pageable pageable,
            @PathVariable("id") @Parameter(description = "ID of the field") Long fieldId
    ) throws ItemNotFoundException {
        return scheduleService.getSchedulesByFieldId(fieldId, pageable);
    }

    @GetMapping(path = "/{id}/schedules/slots", produces = "application/json")
    @Operation(summary = "Get schedule slots for a field on a specific date")
    @ResponseStatus(HttpStatus.OK)
    List<ScheduleSlotDTO> getScheduleSlotsByFieldAndDate(
            @PathVariable("id") Long fieldId,
            @RequestParam("date") @Parameter(description = "Date in yyyy-MM-dd") String dateStr
    ) throws ItemNotFoundException {
        LocalDate date = LocalDate.parse(dateStr);
        return scheduleService.getScheduleSlotsByFieldAndDate(fieldId, date);
    }
}



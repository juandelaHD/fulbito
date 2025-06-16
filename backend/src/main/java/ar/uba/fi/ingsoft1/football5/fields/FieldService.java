package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.filters.FieldFiltersDTO;
import ar.uba.fi.ingsoft1.football5.fields.filters.SpecificationService;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleStatus;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchRepository;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class FieldService {

    private static final String FIELD_ITEM = "field";
    private final FieldRepository fieldRepository;
    private final MatchRepository matchRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final SpecificationService<Field, FieldFiltersDTO> specificationService;

    FieldService(FieldRepository fieldRepository, MatchRepository matchRepository, ImageService imageService,
                 UserService userService, SpecificationService<Field, FieldFiltersDTO> specificationService) {
        this.fieldRepository = fieldRepository;
        this.matchRepository = matchRepository;
        this.imageService = imageService;
        this.userService = userService;
        this.specificationService = specificationService;
    }

    FieldDTO createField(FieldCreateDTO fieldCreate, List<MultipartFile> images, JwtUserDetails userDetails)
            throws IllegalArgumentException, IOException {

        validateUniqueName(fieldCreate, null);
        validateUniqueLocation(fieldCreate, null);

        User owner = userService.loadUserByUsername(userDetails.username());
        Field field = fieldRepository.save(fieldCreate.asField(owner));
        imageService.saveFieldImages(field, images);

        return new FieldDTO(field);
    }

    public Field loadFieldById(Long id) throws ItemNotFoundException {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(FIELD_ITEM, id));
    }

    public boolean isFieldAdmin(Long fieldId, JwtUserDetails userDetails)
            throws ItemNotFoundException, AccessDeniedException {

        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ItemNotFoundException(FIELD_ITEM, fieldId));

        if (!field.getOwner().getUsername().equalsIgnoreCase(userDetails.username())) {
            throw new AccessDeniedException(String.format("User does not have access to field with id '%s'.",
                    fieldId));
        }
        return true;
    }

    public void deleteField(Long id, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(FIELD_ITEM, id));

        validateOwnership(field, userDetails);
        validateNonActiveMatches(field);

        fieldRepository.delete(field);
    }

    public Page<FieldDTO> getFieldsWithFilters(Pageable pageable, JwtUserDetails userDetails, FieldFiltersDTO filters) {
        User owner = userService.loadUserByUsername(userDetails.username());
        Specification<Field> combinedSpec = specificationService.build(filters, owner);

        Page<Field> fieldPage = fieldRepository.findAll(combinedSpec, pageable);
        return fieldPage.map(field -> mapToDTO(field, filters.hasOpenMatch()));
    }

    public FieldDTO updateField(Long id, FieldCreateDTO fieldCreate, List<MultipartFile> images,
                                JwtUserDetails userDetails) throws ItemNotFoundException, IOException {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(FIELD_ITEM, id));

        validateOwnership(field, userDetails);
        validateUniqueName(fieldCreate, id);
        validateUniqueLocation(fieldCreate, id);

        Field saved = fieldRepository.save(fieldCreate.asUpdatedField(field));
        imageService.saveFieldImages(saved, images);
        return new FieldDTO(saved);
    }

    public Page<FieldDTO> getOwnedFields(Pageable pageable, JwtUserDetails userDetails) {
        User owner = userService.loadUserByUsername(userDetails.username());
        Page<Field> fieldPage = fieldRepository.findByOwnerId(owner.getId(), pageable);
        return fieldPage.map(field -> mapToDTO(field, false));
    }

    public Page<FieldDTO> getFieldsWithNonFilters(Pageable pageable) {
        Page<Field> fieldPage = fieldRepository.findAll(pageable);
        return fieldPage.map( field -> mapToDTO(field, false));
    }

    public boolean validateFieldAvailability(
            Long fieldId,
            LocalDate date,
            LocalDateTime startTime,
            LocalDateTime endTime) throws ItemNotFoundException {

        List<Match> matches = matchRepository.findConflictingMatches(fieldId, date, startTime, endTime);

        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ItemNotFoundException(FIELD_ITEM, fieldId));
        boolean slotExists = field.getSchedules().stream()
                .anyMatch(s -> s.getDate().equals(date)
                        && s.getStatus() == ScheduleStatus.AVAILABLE
                        && s.getStartTime().atDate(date).equals(startTime)
                        && s.getEndTime().atDate(date).equals(endTime));

        validateNonConflictingMatches(matches, fieldId, date, startTime, endTime);
        validateSlotAvailability(fieldId, date, startTime, endTime, slotExists);

        return true;
    }

    private void validateNonConflictingMatches(List<Match> matches, Long fieldId, LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
        if (!matches.isEmpty()) {
            throw new IllegalArgumentException(String.format("Field with id '%s' is not available on %s from %s to %s.",
                    fieldId, date, startTime, endTime));
        }
    }

    private void validateSlotAvailability(Long fieldId, LocalDate date, LocalDateTime startTime, LocalDateTime endTime, boolean slotExists) {
        if (!slotExists) {
            throw new IllegalArgumentException(String.format(
                    "No available slot found for field with id '%s' on %s from %s to %s.",
                    fieldId, date, startTime.toLocalTime(), endTime.toLocalTime()));
        }
    }

    private void validateUniqueName(FieldCreateDTO fieldCreate, Long id) {
        fieldRepository.findByName(fieldCreate.name().toLowerCase())
                .filter(field -> !field.getId().equals(id))
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with name '%s' already exists.",
                            fieldCreate.name()));
                });
    }

    private void validateUniqueLocation(FieldCreateDTO fieldCreate, Long id) {
        fieldRepository.findByLocationZoneAndLocationAddress(fieldCreate.zone().toLowerCase(),
                        fieldCreate.address().toLowerCase())
                .filter(field -> !field.getId().equals(id))
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with location '%s, %s' already exists.",
                            fieldCreate.zone(), fieldCreate.address()));
                });
    }

    private void validateNonActiveMatches(Field field) {
        List<Match> futureMatches = matchRepository.findByFieldAndStartTimeAfter(field, LocalDateTime.now());

        boolean hasActiveMatch = futureMatches.stream()
                .anyMatch(match -> match.getStatus() != MatchStatus.FINISHED && match.getStatus() != MatchStatus.CANCELLED);

        if (hasActiveMatch) {
            throw new IllegalArgumentException(String.format(
                    "Field with id '%s' cannot be deleted because it has active matches, " +
                            "but you can disable the field.", field.getId()));
        }
    }

    public void validateOwnership(Field field, JwtUserDetails userDetails) {
        if (!field.getOwner().getUsername().equalsIgnoreCase(userDetails.username())) {
            throw new AccessDeniedException(String.format("User does not have access to field with id '%s'.",
                    field.getId()));
        }
    }

    private FieldDTO mapToDTO(Field field, Boolean includeMatches) {
        // Open matches not requested
        if (!Boolean.TRUE.equals(includeMatches)) {
            return new FieldDTO(field);
        }

        // Open matches requested, with missing players
        Map<LocalDateTime, Integer> matches = field.getMatches().stream()
                .collect(Collectors.toMap(
                        Match::getStartTime,
                        match -> Math.max(0, match.getMaxPlayers() - match.getPlayers().size())));
        return new FieldDTO(field, matches);
    }
}

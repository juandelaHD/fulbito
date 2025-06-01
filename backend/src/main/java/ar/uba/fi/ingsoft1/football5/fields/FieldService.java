package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FieldService {

    private final FieldRepository fieldRepository;
    private final MatchRepository matchRepository;
    private final ImageService imageService;
    private final UserService userService;

    FieldService(FieldRepository fieldRepository, MatchRepository matchRepository, ImageService imageService, UserService userService) {
        this.fieldRepository = fieldRepository;
        this.matchRepository = matchRepository;
        this.imageService = imageService;
        this.userService = userService;
    }

    FieldDTO createField(FieldCreateDTO fieldCreate, List<MultipartFile> images, JwtUserDetails userDetails)
            throws IllegalArgumentException, IOException {

        validateUniqueName(fieldCreate);
        validateUniqueLocation(fieldCreate);

        User owner = userService.loadUserByUsername(userDetails.username());
        Field field = fieldRepository.save(fieldCreate.asField(owner));
        imageService.saveImages(field, images);

        return new FieldDTO(field);
    }

    public FieldDTO getFieldById(Long id) throws ItemNotFoundException {
        return fieldRepository.findById(id)
                .map(FieldDTO::new)
                .orElseThrow(() -> new ItemNotFoundException("field", id));
    }

    public Field loadFieldById(Long id) throws ItemNotFoundException {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("field", id));
    }

    private void validateUniqueName(FieldCreateDTO fieldCreate) {
        fieldRepository.findByName(fieldCreate.name().toLowerCase())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with name '%s' already exists.", fieldCreate.name()));
                });
    }

    private void validateUniqueLocation(FieldCreateDTO fieldCreate) {
        fieldRepository.findByLocationZoneAndLocationAddress(fieldCreate.zone().toLowerCase(), fieldCreate.address().toLowerCase())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with location '%s, %s' already exists.", fieldCreate.zone(), fieldCreate.address()));
                });
    }

    public void deleteField(Long id, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("field", id));

        validateOwnership(field, userDetails);

        // TODO: Here we must add active reservations restriction when implemented.

        fieldRepository.delete(field);
    }

    public boolean validateFieldAvailability(
            Long fieldId,
            LocalDate date,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        List<Match> matches = matchRepository.findConflictingMatches(fieldId, date, startTime, endTime);
        if (!matches.isEmpty()) {
            throw new IllegalArgumentException(String.format("Field with id '%s' is not available on %s from %s to %s.",
                    fieldId, date, startTime, endTime));
        }
        return true;
    }


    private void validateOwnership(Field field, JwtUserDetails userDetails) {
        if (!field.getOwner().getUsername().equalsIgnoreCase(userDetails.username())) {
            throw new AccessDeniedException(String.format("User does not have permission to delete field with id '%s'.", field.getId()));
        }
    }
}

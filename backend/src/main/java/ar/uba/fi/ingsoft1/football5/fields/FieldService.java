package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
class FieldService {

    private final FieldRepository fieldRepository;
    private final ImageService imageService;
    private final UserService userService;

    FieldService(FieldRepository fieldRepository, ImageService imageService, UserService userService) {
        this.fieldRepository = fieldRepository;
        this.imageService = imageService;
        this.userService = userService;
    }

    FieldDTO createField(FieldCreateDTO fieldCreate, List<MultipartFile> images, JwtUserDetails userDetails)
            throws IllegalArgumentException, IOException {
        fieldRepository.findByName(fieldCreate.name())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with name '%s' already exists.", fieldCreate.name()));
                });

        fieldRepository.findByLocationZoneAndLocationAddress(fieldCreate.zone(), fieldCreate.address())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with location '%s, %s' already exists.", fieldCreate.zone(), fieldCreate.address()));
                });

        User owner = userService.getUser(userDetails.username());
        Field field = fieldRepository.save(fieldCreate.asField(owner));
        imageService.saveImages(field, images);
        return new FieldDTO(field);
    }
}

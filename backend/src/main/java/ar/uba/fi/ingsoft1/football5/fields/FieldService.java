package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.images.ImageService;
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

    FieldService(FieldRepository fieldRepository, ImageService imageService) {
        this.fieldRepository = fieldRepository;
        this.imageService = imageService;
    }

    FieldDTO createField(FieldCreateDTO fieldCreate, List<MultipartFile> images) throws IllegalArgumentException, IOException {
        fieldRepository.findByName(fieldCreate.name())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with name '%s' already exists.", fieldCreate.name()));
                });

        fieldRepository.findByLocationZoneAndLocationAddress(fieldCreate.zone(), fieldCreate.address())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with location '%s, %s' already exists.", fieldCreate.zone(), fieldCreate.address()));
                });

        Field field = fieldRepository.save(fieldCreate.asField());
        imageService.saveImages(field, images);
        return new FieldDTO(field);
    }
}

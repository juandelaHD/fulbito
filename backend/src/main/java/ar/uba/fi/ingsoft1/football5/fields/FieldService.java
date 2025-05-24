package ar.uba.fi.ingsoft1.football5.fields;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class FieldService {

    private final FieldRepository fieldRepository;

    FieldService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    FieldDTO createField(FieldCreateDTO fieldCreate) throws IllegalArgumentException {
        fieldRepository.findByName(fieldCreate.name())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with name '%s' already exists.", fieldCreate.name()));
                });

        fieldRepository.findByLocationZoneAndLocationAddress(fieldCreate.zone(), fieldCreate.address())
                .ifPresent(field -> {
                    throw new IllegalArgumentException(String.format("Field with location '%s, %s' already exists.", fieldCreate.zone(), fieldCreate.address()));
                });

        return new FieldDTO(fieldRepository.save(fieldCreate.asField()));
    }
}

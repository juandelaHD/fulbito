package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FieldImageService {

    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public FieldImageService(FieldRepository fieldRepository, UserRepository userRepository) {
        this.fieldRepository = fieldRepository;
        this.userRepository = userRepository;
    }

    public void validateFieldImageOwnership(Image image, JwtUserDetails userDetails)
            throws ItemNotFoundException {
        User user = userRepository.findByUsername(userDetails.username())
                .orElseThrow(() -> new UserNotFoundException("user", userDetails.username()));

        Long fieldId = image.getField().getId();
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ItemNotFoundException("field", fieldId));
        User fieldOwner = field.getOwner();

        if (fieldOwner != null && !fieldOwner.getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("You do not own the field associated with this image.");
        }
    }
}

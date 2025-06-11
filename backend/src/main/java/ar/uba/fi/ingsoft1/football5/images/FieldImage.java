package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("FIELD")
public class FieldImage extends Image {

    @JsonBackReference("field-image")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private Field field;

    @Transient
    private static FieldRepository fieldRepository;

    @Transient
    private static UserRepository userRepository;

    public static void injectRepositories(FieldRepository fieldRepo, UserRepository userRepo) {
        fieldRepository = fieldRepo;
        userRepository = userRepo;
    }

    @Override
    public void validateOwnership(JwtUserDetails userDetails) throws ItemNotFoundException {
        String username = userDetails.username();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("user", username));

        Long fieldId = field.getId();
        Field fieldFromDb = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ItemNotFoundException("field", fieldId));

        if (!fieldFromDb.getOwner().getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("You do not own the field associated with this image.");
        }
    }

    protected FieldImage() {}

    public FieldImage(byte[] data, Field field) {
        super(data);
        this.field = field;
    }

    public Field getField() {
        return field;
    }
}

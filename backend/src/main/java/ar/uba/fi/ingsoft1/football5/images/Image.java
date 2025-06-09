package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "image_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "data", columnDefinition = "BYTEA", nullable = false)
    private byte[] data;

    protected Image() {}

    public Image(byte[] data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public abstract void validateOwnership(JwtUserDetails userDetails) throws ItemNotFoundException;
}

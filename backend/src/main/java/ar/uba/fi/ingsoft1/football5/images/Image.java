package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import jakarta.persistence.*;

@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private Field field;

    protected Image() {}

    protected Image(Long id, byte[] data, Field field) {
        this.id = id;
        this.data = data;
        this.field = field;
    }

    public Image(byte[] data, Field field) {
        this.data = data;
        this.field = field;
    }

    public Long getId() { return id; }
    public byte[] getData() { return data; }
    public Field getField() { return field; }
}

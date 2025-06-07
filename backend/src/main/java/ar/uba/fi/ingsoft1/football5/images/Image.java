package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    @JsonBackReference("field-image")
    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    @JsonBackReference("user-image")
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    protected Image() {}

    public Image(byte[] data, Field field) {
        this.data = data;
        this.field = field;
    }

    public Image(byte[] data, User user) {
        this.data = data;
        this.user = user;
    }

    public Long getId() { return id; }
    protected void setId(Long id) { this.id = id; }
    public byte[] getData() { return data; }
    public Field getField() { return field; }
    public User getUser() { return user; }
}

package ar.uba.fi.ingsoft1.football5.fields.reviews;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 100)
    private String comment;

    @Column(nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    protected Review() {}

    public Review(Integer rating, String comment, Field field, User user) {
        this.rating = rating;
        this.comment = comment;
        this.field = field;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Field getField() {
        return field;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.images.Image;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"zone", "address"}),
        }
)
public class Field {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private GrassType grassType;

    @Column
    private Boolean illuminated;

    @Embedded
    private Location location;

    @JsonManagedReference("field-image")
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @JsonBackReference("match-field")
    @OneToMany(mappedBy = "field", fetch = FetchType.LAZY)
    private List<Match> matches = new ArrayList<>();

    protected Field() {}

    public Field(Long id, String name, GrassType grassType, Boolean illuminated, Location location, User owner) {
        this.id = id;
        this.name = name.toLowerCase();
        this.grassType = grassType;
        this.illuminated = illuminated;
        this.location = location;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GrassType getGrassType() {
        return grassType;
    }

    public Boolean isIlluminated() {
        return illuminated;
    }

    public Location getLocation() {
        return location;
    }

    public List<Image> getImages() {
        return images;
    }

    public User getOwner() {
        return owner;
    }

    public List<Match> getMatches() {
        return matches;
    }
}

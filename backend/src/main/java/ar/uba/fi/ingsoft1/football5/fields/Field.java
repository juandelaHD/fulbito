package ar.uba.fi.ingsoft1.football5.fields;

import ar.uba.fi.ingsoft1.football5.images.Image;
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

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    protected Field() {}

    public Field(Long id, String name, GrassType grassType, Boolean illuminated, Location location) {
        this.id = id;
        this.name = name.toLowerCase();
        this.grassType = grassType;
        this.illuminated = illuminated;
        this.location = location;
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
}

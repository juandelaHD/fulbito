package ar.uba.fi.ingsoft1.football5.fields;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Location(
        @Column
        String zone,
        @Column
        String address
) {
    public Location(String zone, String address) {
        this.zone = zone.toLowerCase();
        this.address = address.toLowerCase();
    }
}
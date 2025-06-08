package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class FieldZoneSpec implements Specification<Field> {

    private final String zone;

    public FieldZoneSpec(String zone, String userZone) {
        this.zone = isBlank(zone) ? userZone : zone;
    }

    @Override
    public Predicate toPredicate(Root<Field> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (zone == null || zone.isBlank()) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.like(criteriaBuilder.lower(root.get("location").get("zone")), "%" + zone.toLowerCase() + "%");
    }
}

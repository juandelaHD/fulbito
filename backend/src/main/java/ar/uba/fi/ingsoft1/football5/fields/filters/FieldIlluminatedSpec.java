package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class FieldIlluminatedSpec implements Specification<Field> {

    private final Boolean illuminated;

    public FieldIlluminatedSpec(Boolean illuminated) {
        this.illuminated = illuminated;
    }

    @Override
    public Predicate toPredicate(Root<Field> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (illuminated == null) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.equal(root.get("illuminated"), illuminated);
    }
}

package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class FieldEnabledSpec implements Specification<Field> {

    private final Boolean enabled;

    public FieldEnabledSpec(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Predicate toPredicate(Root<Field> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if(!Boolean.TRUE.equals(enabled)) {
            return criteriaBuilder.conjunction();
        }

        return criteriaBuilder.equal(root.get("enabled"), enabled);
    }
}

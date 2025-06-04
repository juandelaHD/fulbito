package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.GrassType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class FieldGrassTypeSpec implements Specification<Field> {

    private final GrassType grassType;

    public FieldGrassTypeSpec(GrassType grassType) {
        this.grassType = grassType;
    }

    @Override
    public Predicate toPredicate(Root<Field> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (grassType == null) {
            return criteriaBuilder.conjunction();
        }
        return criteriaBuilder.equal(root.get("grassType"), grassType);
    }
}

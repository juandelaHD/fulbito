package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.user.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class FieldSpecificationService implements SpecificationService<Field, FieldFiltersDTO> {

    @Override
    public Specification<Field> build(FieldFiltersDTO filters, User owner) {
        return Specification.where(new FieldNameSpec(filters.name()))
                .and(new FieldZoneSpec(filters.zone(), owner.getZone()))
                .and(new FieldAddressSpec(filters.address()))
                .and(new FieldGrassTypeSpec(filters.grassType()))
                .and(new FieldIlluminatedSpec(filters.illuminated()))
                .and(new FieldHasOpenScheduledMatchSpec(filters.hasOpenScheduledMatch()));
    }
}

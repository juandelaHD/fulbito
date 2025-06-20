package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.user.User;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationService<I, F> {
    Specification<I> build(F filters, User user);
}

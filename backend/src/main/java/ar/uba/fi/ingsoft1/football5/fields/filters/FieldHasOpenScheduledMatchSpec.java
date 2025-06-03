package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.matches.MatchType;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class FieldHasOpenScheduledMatchSpec implements Specification<Field> {

    private final Boolean hasOpenScheduledMatch;

    public FieldHasOpenScheduledMatchSpec(Boolean hasOpenScheduledMatch) {
        this.hasOpenScheduledMatch = hasOpenScheduledMatch;
    }

    @Override
    public Predicate toPredicate(Root<Field> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if(!Boolean.TRUE.equals(hasOpenScheduledMatch)) {
            return criteriaBuilder.conjunction();
        }

        query.distinct(true);
        Join<Field, Match> joinMatch = root.join("matches", JoinType.INNER);
        Predicate openPredicate = criteriaBuilder.equal(joinMatch.get("type"), MatchType.OPEN);
        Predicate scheduledPredicate = criteriaBuilder.equal(joinMatch.get("status"), MatchStatus.SCHEDULED);
        // La fecha sea en el futuro
        return criteriaBuilder.and(openPredicate, scheduledPredicate);
    }
}

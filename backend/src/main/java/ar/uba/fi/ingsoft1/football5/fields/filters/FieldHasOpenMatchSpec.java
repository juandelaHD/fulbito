package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.matches.MatchType;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class FieldHasOpenMatchSpec implements Specification<Field> {

    private final Boolean hasOpenMatch;

    public FieldHasOpenMatchSpec(Boolean hasOpenMatch) {
        this.hasOpenMatch = hasOpenMatch;
    }

    @Override
    public Predicate toPredicate(Root<Field> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if(!Boolean.TRUE.equals(hasOpenMatch)) {
            return criteriaBuilder.conjunction();
        }

        query.distinct(true);
        Join<Field, Match> joinMatch = root.join("matches", JoinType.INNER);
        Predicate openPredicate = criteriaBuilder.equal(joinMatch.get("type"), MatchType.OPEN);
        Predicate pendingPredicate = criteriaBuilder.equal(joinMatch.get("status"), MatchStatus.PENDING);
        Predicate futurePredicate = criteriaBuilder.greaterThan(joinMatch.get("startTime"), criteriaBuilder.currentTimestamp());
        return criteriaBuilder.and(openPredicate, pendingPredicate, futurePredicate);
    }
}

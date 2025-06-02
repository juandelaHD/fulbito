package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.matches.MatchType;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldHasOpenScheduledMatchSpecTest {

    @Mock
    private Root<Field> mockRoot;
    @Mock
    private CriteriaQuery<?> mockQuery;
    @Mock
    private CriteriaBuilder mockCb;

    @Mock
    private Join<Field, Match> mockJoin;
    @Mock
    private Path<MatchType> mockTypePath;
    @Mock
    private Path<MatchStatus> mockStatusPath;

    @Mock
    private Predicate mockConjunctionPredicate;
    @Mock
    private Predicate mockTypePredicate;
    @Mock
    private Predicate mockStatusPredicate;
    @Mock
    private Predicate mockAndPredicate;

    @Test
    void toPredicate_whenHasOpenScheduledMatchIsFalse_returnsConjunction() {
        FieldHasOpenScheduledMatchSpec spec = new FieldHasOpenScheduledMatchSpec(false);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenHasOpenScheduledMatchIsNull_returnsConjunction() {
        FieldHasOpenScheduledMatchSpec spec = new FieldHasOpenScheduledMatchSpec(null);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenHasOpenScheduledMatchIsTrue_returnsAndPredicate() {
        FieldHasOpenScheduledMatchSpec spec = new FieldHasOpenScheduledMatchSpec(true);

        when(mockRoot.<Field, Match>join("matches", JoinType.INNER)).thenReturn(mockJoin);
        when(mockJoin.<MatchType>get("type")).thenReturn(mockTypePath);
        when(mockJoin.<MatchStatus>get("status")).thenReturn(mockStatusPath);
        when(mockCb.equal(mockTypePath, MatchType.OPEN)).thenReturn(mockTypePredicate);
        when(mockCb.equal(mockStatusPath, MatchStatus.SCHEDULED)).thenReturn(mockStatusPredicate);
        when(mockCb.and(mockTypePredicate, mockStatusPredicate)).thenReturn(mockAndPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockAndPredicate, result);
    }
}

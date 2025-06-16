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

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldHasOpenMatchSpecTest {

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
    private Path<Timestamp> mockStartTimePath;

    @Mock
    private Predicate mockConjunctionPredicate;
    @Mock
    private Predicate mockTypePredicate;
    @Mock
    private Predicate mockStatusPredicate;
    @Mock
    private Predicate mockStartTimePredicate;
    @Mock
    private Predicate mockAndPredicate;

    @Test
    void toPredicate_whenHasOpenMatchIsFalse_returnsConjunction() {
        FieldHasOpenMatchSpec spec = new FieldHasOpenMatchSpec(false);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenHasOpenMatchIsNull_returnsConjunction() {
        FieldHasOpenMatchSpec spec = new FieldHasOpenMatchSpec(null);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenHasOpenMatchIsTrue_returnsAndPredicate() {
        FieldHasOpenMatchSpec spec = new FieldHasOpenMatchSpec(true);

        when(mockRoot.<Field, Match>join("matches", JoinType.INNER)).thenReturn(mockJoin);
        when(mockJoin.<MatchType>get("type")).thenReturn(mockTypePath);
        when(mockJoin.<MatchStatus>get("status")).thenReturn(mockStatusPath);
        when(mockJoin.<Timestamp>get("startTime")).thenReturn(mockStartTimePath);
        when(mockCb.equal(mockTypePath, MatchType.OPEN)).thenReturn(mockTypePredicate);
        when(mockCb.equal(mockStatusPath, MatchStatus.ACCEPTED)).thenReturn(mockStatusPredicate);
        when(mockCb.greaterThan(mockStartTimePath, mockCb.currentTimestamp())).thenReturn(mockStartTimePredicate);
        when(mockCb.and(mockTypePredicate, mockStatusPredicate, mockStartTimePredicate)).thenReturn(mockAndPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockAndPredicate, result);
    }
}

package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.Location;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldZoneSpecTest {

    @Mock
    private Root<Field> mockRoot;
    @Mock
    private CriteriaQuery<?> mockQuery;
    @Mock
    private CriteriaBuilder mockCb;

    @Mock
    private Path<Location> mockLocationPath;
    @Mock
    private Path<String> mockZonePath;
    @Mock
    private Path<String> mockLowerExpression;

    @Mock
    private Predicate mockConjunctionPredicate;
    @Mock
    private Predicate mockEqualPredicate;

    @Test
    void toPredicate_whenBothNullZones_returnsConjunction() {
        FieldZoneSpec spec = new FieldZoneSpec(null, null);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenBothBlankZones_returnsConjunction() {
        FieldZoneSpec spec = new FieldZoneSpec("", " ");
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenNullZone_returnsLikePredicateUsingUserZone() {
        String userZone = "user zone";
        FieldZoneSpec spec = new FieldZoneSpec(null, userZone);

        when(mockRoot.<Location>get("location")).thenReturn(mockLocationPath);
        when(mockLocationPath.<String>get("zone")).thenReturn(mockZonePath);

        when(mockCb.lower(mockZonePath)).thenReturn(mockLowerExpression);
        when(mockCb.like(mockLowerExpression, "%" + userZone.toLowerCase() + "%")).thenReturn(mockEqualPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);

        assertSame(mockEqualPredicate, result);
    }

    @Test
    void toPredicate_whenBlankZone_returnsLikePredicateUsingUserZone() {
        String userZone = "user zone";
        FieldZoneSpec spec = new FieldZoneSpec(" ", userZone);

        when(mockRoot.<Location>get("location")).thenReturn(mockLocationPath);
        when(mockLocationPath.<String>get("zone")).thenReturn(mockZonePath);

        when(mockCb.lower(mockZonePath)).thenReturn(mockLowerExpression);
        when(mockCb.like(mockLowerExpression, "%" + userZone.toLowerCase() + "%")).thenReturn(mockEqualPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);

        assertSame(mockEqualPredicate, result);
    }

    @Test
    void toPredicate_whenValidZone_returnsLikePredicateUsingZone() {
        String userZone = "user zone";
        String zone = "test zone";
        FieldZoneSpec spec = new FieldZoneSpec(zone, userZone);

        when(mockRoot.<Location>get("location")).thenReturn(mockLocationPath);
        when(mockLocationPath.<String>get("zone")).thenReturn(mockZonePath);

        when(mockCb.lower(mockZonePath)).thenReturn(mockLowerExpression);
        when(mockCb.like(mockLowerExpression, "%" + zone.toLowerCase() + "%")).thenReturn(mockEqualPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);

        assertSame(mockEqualPredicate, result);
    }
}

package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.Location;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldAddressSpecTest {

    @Mock
    private Root<Field> mockRoot;
    @Mock
    private CriteriaQuery<?> mockQuery;
    @Mock
    private CriteriaBuilder mockCb;

    @Mock
    private Path<Location> mockLocationPath;
    @Mock
    private Path<String> mockAddressPath;
    @Mock
    private Path<String> mockLowerExpression;

    @Mock
    private Predicate mockConjunctionPredicate;
    @Mock
    private Predicate mockEqualPredicate;

    @Test
    void toPredicate_whenNullAddress_returnsConjunction() {
        FieldAddressSpec spec = new FieldAddressSpec(null);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenBlankAddress_returnsConjunction() {
        FieldAddressSpec spec = new FieldAddressSpec(" ");
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenValidAddress_returnsLikePredicate() {
        String address = "test address";
        FieldAddressSpec spec = new FieldAddressSpec(address);

        when(mockRoot.<Location>get("location")).thenReturn(mockLocationPath);
        when(mockLocationPath.<String>get("address")).thenReturn(mockAddressPath);

        when(mockCb.lower(mockAddressPath)).thenReturn(mockLowerExpression);
        when(mockCb.like(mockLowerExpression, "%" + address.toLowerCase() + "%")).thenReturn(mockEqualPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);

        assertSame(mockEqualPredicate, result);
    }
}

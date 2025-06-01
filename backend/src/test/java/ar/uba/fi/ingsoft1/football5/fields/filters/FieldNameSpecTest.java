package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldNameSpecTest {

    @Mock
    private Root<Field> mockRoot;
    @Mock
    private CriteriaQuery<?> mockQuery;
    @Mock
    private CriteriaBuilder mockCb;

    @Mock
    private Path<String> mockNamePath;
    @Mock
    private Path<String> mockLowerExpression;

    @Mock
    private Predicate mockConjunctionPredicate;
    @Mock
    private Predicate mockEqualPredicate;

    @Test
    void toPredicate_whenNullName_returnsConjunction() {
        FieldNameSpec spec = new FieldNameSpec(null);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenBlankName_returnsConjunction() {
        FieldNameSpec spec = new FieldNameSpec(" ");
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenValidName_returnsLikePredicate() {
        String name = "test name";
        FieldNameSpec spec = new FieldNameSpec(name);

        when(mockRoot.<String>get("name")).thenReturn(mockNamePath);

        when(mockCb.lower(mockNamePath)).thenReturn(mockLowerExpression);
        when(mockCb.like(mockLowerExpression, "%" + name.toLowerCase() + "%")).thenReturn(mockEqualPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockEqualPredicate, result);
    }
}

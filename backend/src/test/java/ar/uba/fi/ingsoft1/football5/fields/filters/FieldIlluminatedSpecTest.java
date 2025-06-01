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
class FieldIlluminatedSpecTest {

    @Mock
    private Root<Field> mockRoot;
    @Mock
    private CriteriaQuery<?> mockQuery;
    @Mock
    private CriteriaBuilder mockCb;

    @Mock
    private Path<Boolean> mockIlluminatedPath;

    @Mock
    private Predicate mockConjunctionPredicate;
    @Mock
    private Predicate mockEqualPredicate;

    @Test
    void toPredicate_whenNullIlluminated_returnsConjunction() {
        FieldIlluminatedSpec spec = new FieldIlluminatedSpec(null);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenValidIlluminated_returnsLikePredicate() {
        Boolean illuminated = true;
        FieldIlluminatedSpec spec = new FieldIlluminatedSpec(illuminated);

        when(mockRoot.<Boolean>get("illuminated")).thenReturn(mockIlluminatedPath);
        when(mockCb.equal(mockIlluminatedPath, illuminated)).thenReturn(mockEqualPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockEqualPredicate, result);
    }
}

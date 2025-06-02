package ar.uba.fi.ingsoft1.football5.fields.filters;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.GrassType;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldGrassTypeSpecTest {

    @Mock
    private Root<Field> mockRoot;
    @Mock
    private CriteriaQuery<?> mockQuery;
    @Mock
    private CriteriaBuilder mockCb;

    @Mock
    private Path<String> mockGrassTypePath;

    @Mock
    private Predicate mockConjunctionPredicate;
    @Mock
    private Predicate mockEqualPredicate;

    @Test
    void toPredicate_whenNullGrassType_returnsConjunction() {
        FieldGrassTypeSpec spec = new FieldGrassTypeSpec(null);
        when(mockCb.conjunction()).thenReturn(mockConjunctionPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockConjunctionPredicate, result);
    }

    @Test
    void toPredicate_whenValidGrassType_returnsLikePredicate() {
        GrassType grassType = GrassType.NATURAL_GRASS;
        FieldGrassTypeSpec spec = new FieldGrassTypeSpec(grassType);

        when(mockRoot.<String>get("grassType")).thenReturn(mockGrassTypePath);
        when(mockCb.equal(mockGrassTypePath, grassType)).thenReturn(mockEqualPredicate);

        Predicate result = spec.toPredicate(mockRoot, mockQuery, mockCb);
        assertSame(mockEqualPredicate, result);
    }
}

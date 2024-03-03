package rs.ac.bg.fon.tps_backend.mapper;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.bg.fon.tps_backend.domain.City;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CityRowMappingTest {

    private static CityRowMapper mapper;
    @Mock
    private ResultSet rs;
    @BeforeAll
    static void beforeAll() {
        mapper = new CityRowMapper();
    }

    @AfterAll
    static void afterAll() {
        mapper = null;
    }

    @Test
    @DisplayName("Empty result set mapping")
    public void emptyResultSetMappingTest() throws SQLException {
        assertThatThrownBy(()->mapper.mapRow(null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Result set may not be null.");
    }

    @Test
    @DisplayName("Regular mapping of result set")
    public void normalResultSetMappingTest() throws SQLException {
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Beograd");
        when(rs.getInt(any(String.class))).thenAnswer(invocation -> {
            String columnName = invocation.getArgument(0);
            if ("pptbr".equals(columnName)) {
                return 11_000;
            } else if ("population".equals(columnName)) {
                return 1_370_000;
            } else {
                throw new IllegalArgumentException("Unexpected column name: " + columnName);
            }
        });

        val expectedCity = new City(1L, 11_000,
                "Beograd", 1_370_000);

        assertThat(expectedCity)
                .isEqualTo(mapper.mapRow(rs, 0));
    }


}

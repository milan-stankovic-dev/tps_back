package rs.ac.bg.fon.tps_backend.converter.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.dto.CityDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class CityConverterTest {
    private final CityConverter cityConverter;

    @Autowired
    public CityConverterTest(CityConverter cityConverter) {
        this.cityConverter = cityConverter;
    }

    @Test
    @DisplayName("To entity null")
    void toEntityNullTest() {
        final City expectedResult = null;
        final City actualResult = cityConverter
                .toEntity(null);

        assertThat(expectedResult)
                .isEqualTo(actualResult);
    }
    @Test
    @DisplayName("To entity normal")
    void toEntityNormalTest() {
        final City expectedResult = City.builder()
                .id(1L)
                .name("Test city")
                .build();
        final City actualResult = cityConverter
                .toEntity(new CityDTO(1L, "Test city"));

        assertThat(expectedResult)
                .isEqualTo(actualResult);
    }
    @Test
    @DisplayName("To dto null")
    void toDtoNullTest() {
        final CityDTO expectedResult = null;
        final CityDTO actualResult = cityConverter
                .toDto(null);

        assertThat(expectedResult)
                .isEqualTo(actualResult);
    }
    @Test
    @DisplayName("To dto normal")
    void toDtoNormalTest() {
        final CityDTO expectedResult
                = new CityDTO(1L, "Test city");
        final CityDTO actualResult = cityConverter
                .toDto(City.builder()
                        .id(1L)
                        .name("Test city")
                        .build());

        assertThat(expectedResult)
                .isEqualTo(actualResult);
    }

}

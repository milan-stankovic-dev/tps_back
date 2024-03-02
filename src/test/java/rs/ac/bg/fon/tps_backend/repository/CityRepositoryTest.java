package rs.ac.bg.fon.tps_backend.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.tps_backend.domain.City;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CityRepositoryTest {
    @Autowired
    private CityRepository cityRepository;
    private static final City exampleCity = new City(
            1L, 15_000, "Bor", 20_000
    );
    @BeforeEach
    void setUp() {
        cityRepository.save(exampleCity);
    }

    @AfterEach
    void tearDown() {
        cityRepository.deleteAll();
    }

    @Test
    @DisplayName("Find city by ptpbr not found")
    void selectPtpbrTestingNotFound() {
        final Optional<City> foundCityOpt =
                cityRepository.findByPptbr(11_000);
        assertThat(foundCityOpt).isEmpty();
    }

    @Test
    @DisplayName("Find city by ptpbr found")
    void selectPtpbrTestingFound() {
        final Optional<City> foundCityOpt =
                cityRepository.findByPptbr(15_000);
        assertThat(foundCityOpt).isNotEmpty().contains(exampleCity);
    }
}

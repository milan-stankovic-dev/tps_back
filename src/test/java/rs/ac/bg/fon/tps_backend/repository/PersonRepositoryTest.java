package rs.ac.bg.fon.tps_backend.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PersonRepositoryTest {
    private static City city1;
    private static City city2;
    private static Person person;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CityRepository cityRepository;

    @BeforeAll
    static void beforeAll() {
        city1 = new City(
          1L, 11_000, "Belgrade", 2_100_000
        );
        city2 = new City(
                2L, 19_000, "Zajecar", 30_000
        );
        person = new Person(
                1L, "Pera", "Peric", LocalDate.of(2000,1,1),
                0, city2, city1
        );

    }

    @BeforeEach
    void setUp() {
        cityRepository.save(city1);
        cityRepository.save(city2);
        personRepository.save(person);
    }

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
        cityRepository.deleteAll();
    }

    @Test
    @DisplayName("Normal case for saving a person to the db.")
    void saveTesting() {
        final boolean exists = personRepository.existsById(1L);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Selecting from the database")
    void selectTesting() {
        final var persons = personRepository.findAll();

        assertThat(persons).isNotEmpty().contains(person);
    }
}

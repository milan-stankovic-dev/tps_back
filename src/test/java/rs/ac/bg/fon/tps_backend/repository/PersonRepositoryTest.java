package rs.ac.bg.fon.tps_backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
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
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1),
                0, city2, city1
        );

    }

    @AfterAll
    static void afterAll() {
        city1 = null;
        city2 = null;
        person = null;
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
        val personDB = personRepository.save(person);

        assertThat(personDB).isEqualTo(person);
    }

    @Test
    @DisplayName("Selecting from the database")
    void selectTesting() {
        val persons = personRepository.findAll();

        assertThat(persons).isNotEmpty().contains(person);
    }

    @Test
    @DisplayName("Selecting by id")
    void selectByIdTesting() {
        val personDbOptional = personRepository.existsById(1L);

        assertThat(personDbOptional).isTrue();
    }
}

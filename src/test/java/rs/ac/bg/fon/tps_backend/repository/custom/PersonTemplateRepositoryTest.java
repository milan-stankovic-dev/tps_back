package rs.ac.bg.fon.tps_backend.repository.custom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.repository.CityRepository;
import rs.ac.bg.fon.tps_backend.repository.custom.DatabaseConnector;
import rs.ac.bg.fon.tps_backend.repository.custom.PersonTemplateRepository;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PersonTemplateRepositoryTest {

    @Autowired
    private PersonTemplateRepository repository;

    @MockBean
    private Connection conn;

    @MockBean
    private DatabaseConnector connector;

    @MockBean
    private CallableStatement cs;

    @MockBean
    private CityRepository cityRepository;

    private final City city1 = new City(
            1L, 11_000, "Belgrade", 2_100_000
    );
    private final Person person = new Person(
            1L, "Pera", "Peric", 189,
            LocalDate.of(2000,1,1),
            260, city1, city1);

    @BeforeEach
    void setUp() throws SQLException, IOException {
        when(connector.getConnection("application.properties"))
                .thenReturn(conn);
        when(conn.prepareCall("CALL select_person_by_id(?,?)"))
                .thenReturn(cs);
        when(cs.execute()).thenReturn(true);
    }

    @Test
    @DisplayName("get person by id null id")
    void getPersonByIdNullId() {
        assertThatThrownBy(() -> repository.getPersonById(null, "DoesNotMatter"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Id may not be null.");
    }

    @Test
    @DisplayName("get person by id null query")
    void getPersonByIdNullQuery() {
        assertThatThrownBy(() -> repository.getPersonById(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Query may not be null or empty.");
    }

    @Test
    @DisplayName("Gets person by id normally")
    void getPersonByIdNormallyTest() throws SQLException, IOException, ParseException {
        when(cs.getObject(2)).thenReturn(
                "(1,Pera,Peric,189,2000-01-01,260,1,1)");

        when(cityRepository.findById(1L))
                .thenReturn(Optional.of(city1));

        assertThat(repository.getPersonById(1L,
                "CALL select_person_by_id(?,?)")).isNotEmpty()
                .contains(person);
    }

    @Test
    @DisplayName("Person does not exist by id")
    void personDoesNotExistByIdTest() throws SQLException, IOException, ParseException {
        when(cs.getObject(2)).thenReturn(null);

        assertThat(repository.getPersonById(1L,
                "CALL select_person_by_id(?,?)"))
                .isEmpty();
    }
}

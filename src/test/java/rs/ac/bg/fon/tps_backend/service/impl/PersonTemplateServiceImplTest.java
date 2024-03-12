package rs.ac.bg.fon.tps_backend.service.impl;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonDisplayConverter;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonSaveConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.CityDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;
import rs.ac.bg.fon.tps_backend.exception.UnknownCityException;
import rs.ac.bg.fon.tps_backend.lambda.UpdateQuery;
import rs.ac.bg.fon.tps_backend.mapper.CityRowMapper;
import rs.ac.bg.fon.tps_backend.mapper.PersonRowMapper;
import rs.ac.bg.fon.tps_backend.service.PersonService;
import rs.ac.bg.fon.tps_backend.validator.PersonValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PersonTemplateServiceImplTest {
    @MockBean
    private JdbcTemplate jdbcTemplate;
    @MockBean
    private PersonRowMapper personMapper;
    @MockBean
    private CityRowMapper cityMapper;
    @MockBean
    private UpdateQuery updateQuery;
    private final PersonService personService;
    @MockBean
    private PersonSaveConverter personSaveConverter;
    @MockBean
    private PersonDisplayConverter personDisplayConverter;
    @MockBean
    private PersonValidator personValidator;

    @Autowired
    PersonTemplateServiceImplTest(@Qualifier(value = "personTemplateServiceImpl")
                                  PersonService personService) {
        this.personService = personService;
    }

    @Test
    @DisplayName("Select all persons empty")
    void selectAllPersonsEmpty() {
        val personList = personService.getAll();

        assertThat(personList).isEmpty();
        verify(jdbcTemplate,
                times(1)).query(
                        eq("SELECT * FROM select_persons()"),
                        eq(personMapper)
        );
    }

    @Test
    @DisplayName("Saves person successfully!")
    void savePersonVerificationWorks() throws Exception {
        final PersonSaveDTO personSaveDTO = new PersonSaveDTO(
                1L, "Pera", "Peric",
                189,
                LocalDate.of(2000, 1,1),
                11_000, 19_000
        );
        val cityBirth = City.builder()
                .id(1L)
                .name("Beograd")
                .pptbr(11_000)
                .population(1_370_000)
                .build();
        val cityResidence = City.builder()
                .id(2L)
                .name("Zaječar")
                .pptbr(19_000)
                .population(30_000)
                .build();

        final Person person = new Person(1L, "Pera", "Peric",
                189, LocalDate.of(2000,1,1),0,
                cityBirth,cityResidence);

        doNothing()
                .when(personValidator).validateForSave(personSaveDTO);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_city_by_pptbr(?)"),
                eq(cityMapper),
                any(Integer.class)))
                .thenAnswer(invocation -> {
                    if ((int) invocation.getArgument(2) == 11_000) {
                        return cityBirth;
                    } else {
                        return cityResidence;
                    }
                });

        when(personSaveConverter.toEntity(personSaveDTO))
                .thenReturn(person);
        when(personSaveConverter.toDto(person))
                .thenReturn(personSaveDTO);
        when(personValidator.setLastNameToJovanovicDefault(personSaveDTO))
                .thenReturn(personSaveDTO);

        final PersonSaveDTO personSaved = personService.savePerson(personSaveDTO);
        assertThat(personSaved).isEqualTo(personSaveDTO);

        verify(jdbcTemplate,times(1))
                .update(
                        eq("CALL insert_person(?, ?, ?, ?, ?, ?, ?)"),
                        eq(person.getFirstName()),
                        eq(person.getLastName()),
                        eq(person.getHeightInCm()),
                        eq(person.getDOB()),
                        eq(person.getAgeInMonths()),
                        eq(person.getCityOfBirth().getId()),
                        eq(person.getCityOfResidence().getId())
                );

        verify(jdbcTemplate,times(2))
                .queryForObject(
                        eq("SELECT * FROM select_city_by_pptbr(?)"),
                        eq(cityMapper),
                        any(Integer.class));

        verify(personValidator, times(1))
                .validateForSave(personSaveDTO);
        verify(personSaveConverter, times(1))
                .toEntity(personSaveDTO);
        verify(personSaveConverter, times(1))
                .toDto(any(Person.class));
    }

    @Test
    @DisplayName("Throws exception due to unknown city of birth")
    void saveUnknownCityOfBirth() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "Pera", "Peric",
                189,
                LocalDate.of(2000,1,1),
                -1, 11_000
        );

        when(personValidator.setLastNameToJovanovicDefault(person))
                .thenReturn(person);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_city_by_pptbr(?)"),
                eq(cityMapper),
                eq(person.birthCityCode()))).thenReturn(null);

        assertThatThrownBy(()-> personService.savePerson(person))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");
        verify(updateQuery, never()).updateQuery(any(Person.class));
    }

    @Test
    @DisplayName("Throws exception due to unknown city of residence")
    void saveUnknownCityOfResidence() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "Pera", "Peric",
                189,
                LocalDate.of(2000,1,1),
                19_000, -1
        );

        when(personValidator.setLastNameToJovanovicDefault(person))
                .thenReturn(person);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_city_by_pptbr(?)"),
                eq(cityMapper),
                any(Integer.class)))
                .thenAnswer(invocation -> {
                    final int pptbr = invocation.getArgument(2);
                    return switch (pptbr){
                        case 19_000 -> new City(1L, pptbr,
                                "Zajecar", 30_0000);
                        case -1 -> null;
                        default -> throw new IllegalArgumentException("Unknown argument " + pptbr);
                    };
                });

        assertThatThrownBy(()-> personService.savePerson(person))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");
        verify(updateQuery, never()).updateQuery(any(Person.class));
    }

    @Test
    @DisplayName("Invalid person passed to save")
    void invalidPersonInputTest() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "P", "Peric",
                189,
                LocalDate.of(2000,1,1),
                -1, 11_000
        );
        doThrow(new PersonNotInitializedException("Person's name length is invalid."))
                .when(personValidator).validateForSave(person);

        assertThatThrownBy(()-> personService.savePerson(person))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name length is invalid.");
        verify(personValidator, times(1))
                .validateForSave(person);
        verify(updateQuery, never()).updateQuery(
               any(Person.class));
    }
    @Test
    @DisplayName("Delete person null")
    void deleteNullPersonId() {
        assertThatThrownBy(()->personService.deletePerson(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Your id may not be null.");
        verify(jdbcTemplate, never()).update(
                eq("CALL delete_person(?)"),
                        any(Integer.class));
    }

    @Test
    @DisplayName("Delete person non null")
    void deletePerson() throws Exception {
        val person = new Person(1L, "Pera","Peric",189,
                LocalDate.of(2000,1,1),
                0, new City(), new City());

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_person_by_id(?)"),
                eq(personMapper), eq(1L)
        )).thenReturn(person);

        personService.deletePerson(1L);

        verify(jdbcTemplate, times(1))
                .update(
                        eq("CALL delete_person(?)"),
                        eq(1L)
                );
    }

    @Test
    @DisplayName("Throws exception due to null id for person to update")
    void updateNullIdPerson() throws Exception {
        final PersonSaveDTO personSaveDTO = new PersonSaveDTO(
                null, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );
        final Person person = new Person(
                null, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1),0,
                City.builder().pptbr(11_000).build(), City.builder().pptbr(19_0000).build()
        );

        doThrow(new PersonNotInitializedException("Your person's ID may not be null."))
                .when(personValidator).validateUpdateId(personSaveDTO);

        assertThatThrownBy(()->personService.updatePerson(personSaveDTO))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Your person's ID may not be null.");

        verify(jdbcTemplate,never())
                .update(
                        eq("CALL insert_person(?, ?, ?, ?, ?, ?, ?, ?)"),
                        eq(person.getId()),
                        eq(person.getFirstName()),
                        eq(person.getLastName()),
                        eq(person.getHeightInCm()),
                        eq(person.getDOB()),
                        eq(person.getAgeInMonths()),
                        eq(person.getCityOfBirth().getId()),
                        eq(person.getCityOfResidence().getId())
                );
        verify(personValidator, times(1))
                .validateUpdateId(personSaveDTO);
    }

    @Test
    @DisplayName("Throws exception after trying to update null person")
    void updateNonExistingPerson() throws Exception {
        doThrow(new PersonNotInitializedException("Your person may not be null."))
                .when(personValidator).validateForSave(null);

        assertThatThrownBy(() -> personService.updatePerson(null))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Your person may not be null.");

        verify(jdbcTemplate,never())
                .update(
                        eq("CALL insert_person(?, ?, ?, ?, ?, ?, ?, ?)"),
                        any(Long.class),
                        any(String.class),
                        any(String.class),
                        any(Integer.class),
                        any(LocalDate.class),
                        any(Integer.class),
                        any(Long.class),
                        any(Long.class)
                );
    }

    @Test
    @DisplayName("Throws exception due to unknown city of birth")
    void updateUnknownCityOfBirth() throws Exception {
        final PersonSaveDTO personSaveDTO = new PersonSaveDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );
        final Person person = new Person(
                1L, "Pera","Peric",189,
                LocalDate.of(2000,1,1), 0,
                City.builder().pptbr(11_000).build(),
                City.builder().pptbr(19_000).build()
        );

        doNothing().when(personValidator)
                .validateUpdateId(personSaveDTO);
        doNothing().when(personValidator)
                .validateForSave(personSaveDTO);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_person_by_id(?)"),
                eq(personMapper),
                eq(personSaveDTO.id())
        )).thenReturn(person);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_city_by_pptbr(?)"),
                eq(cityMapper),
                eq(personSaveDTO.birthCityCode()))).thenReturn(null);

        when(personValidator.setLastNameToJovanovicDefault(personSaveDTO))
                .thenReturn(personSaveDTO);

        assertThatThrownBy(()->personService.updatePerson(personSaveDTO))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");

        verify(jdbcTemplate,never())
                .update(
                        eq("CALL insert_person(?, ?, ?, ?, ?, ?, ?, ?)"),
                        any(Long.class),
                        any(String.class),
                        any(String.class),
                        any(Integer.class),
                        any(LocalDate.class),
                        any(Integer.class),
                        any(Long.class),
                        any(Long.class)
                );
    }

    @Test
    @DisplayName("Throws exception due to unknown city of residence")
    void updateUnknownCityOfResidence() throws Exception {
        final PersonSaveDTO personSaveDTO = new PersonSaveDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );
        final Person person = new Person(
                1L, "Pera","Peric",189,
                LocalDate.of(2000,1,1), 0,
                City.builder().pptbr(11_000).build(),
                City.builder().pptbr(19_000).build()
        );

        doNothing().when(personValidator)
                .validateUpdateId(personSaveDTO);
        doNothing().when(personValidator)
                .validateForSave(personSaveDTO);
        when(personValidator.setLastNameToJovanovicDefault(personSaveDTO))
                .thenReturn(personSaveDTO);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_person_by_id(?)"),
                eq(personMapper),
                eq(personSaveDTO.id())
        )).thenReturn(person);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_city_by_pptbr(?)"),
                eq(cityMapper),
                eq(personSaveDTO.birthCityCode())))
                .thenAnswer(invocationOnMock -> {
                    val pptbr = invocationOnMock.getArgument(2);
                    return switch ((int)pptbr){
                        case 11_000 -> City.builder().pptbr(11_000).build();
                        case 19_000 -> null;
                        default ->
                            throw new IllegalArgumentException("Unknown value in answer: " + pptbr);
                    };
                });

        assertThatThrownBy(()->personService.updatePerson(personSaveDTO))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");

        verify(jdbcTemplate,never())
                .update(
                        eq("CALL insert_person(?, ?, ?, ?, ?, ?, ?, ?)"),
                        any(Long.class),
                        any(String.class),
                        any(String.class),
                        any(Integer.class),
                        any(LocalDate.class),
                        any(Integer.class),
                        any(Long.class),
                        any(Long.class)
                );
    }

    @Test
    @DisplayName("Invalid person passed to update")
    void invalidPersonInputUpdateTest() throws Exception {
        final PersonSaveDTO personSaveDTO = new PersonSaveDTO(
                1L, "P", "Peric",
                189,
                LocalDate.of(2000,1,1),
                -1, 11_000
        );

        doThrow(new PersonNotInitializedException("Person's name length is invalid."))
                .when(personValidator).validateForSave(personSaveDTO);
        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_person_by_id(?)"),
                eq(personMapper),
                eq(personSaveDTO.id())
        )).thenReturn(new Person());

        assertThatThrownBy(()-> personService.updatePerson(personSaveDTO))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name length is invalid.");
        verify(personValidator, times(1))
                .validateForSave(personSaveDTO);
        verify(personValidator,times(1))
                .validateUpdateId(personSaveDTO);
        verify(jdbcTemplate,never())
                .update(
                        eq("CALL insert_person(?, ?, ?, ?, ?, ?, ?, ?)"),
                        any(Long.class),
                        any(String.class),
                        any(String.class),
                        any(Integer.class),
                        any(LocalDate.class),
                        any(Integer.class),
                        any(Long.class),
                        any(Long.class)
                );
    }

    @Test
    @DisplayName("Updates person successfully!")
    void updatePersonVerificationWorks() throws Exception {
        final PersonSaveDTO personSaveDTO = new PersonSaveDTO(
                1L, "Pera", "Peric",
                189,
                LocalDate.of(2000, 1,1),
                11_000, 19_000
        );
        val cityBirth = City.builder()
                .id(1L)
                .name("Beograd")
                .pptbr(11_000)
                .population(1_370_000)
                .build();
        val cityResidence = City.builder()
                .id(2L)
                .name("Zaječar")
                .pptbr(19_000)
                .population(30_000)
                .build();

        final Person person = new Person(1L, "Pera", "Peric",
                189, LocalDate.of(2000,1,1),0,
                cityBirth,cityResidence);

        doNothing().when(personValidator)
                .validateUpdateId(personSaveDTO);
        doNothing().when(personValidator)
                .validateForSave(personSaveDTO);
        when(personValidator.setLastNameToJovanovicDefault(personSaveDTO))
                .thenReturn(personSaveDTO);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_person_by_id(?)"),
                eq(personMapper),
                eq(personSaveDTO.id())
        )).thenReturn(person);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_city_by_pptbr(?)"),
                eq(cityMapper),
                any(Integer.class)))
                .thenAnswer(invocation -> {
                    if ((int) invocation.getArgument(2) == 11_000) {
                        return cityBirth;
                    } else {
                        return cityResidence;
                    }
                });

        when(personSaveConverter.toEntity(personSaveDTO))
                .thenReturn(person);
        when(personSaveConverter.toDto(person))
                .thenReturn(personSaveDTO);
        final PersonSaveDTO personSaved = personService.updatePerson(personSaveDTO);
        assertThat(personSaved).isEqualTo(personSaveDTO);

        verify(jdbcTemplate,times(1))
                .update(
                        eq("CALL update_person(?, ?, ?, ?, ?, ?, ?, ?)"),
                        eq(person.getId()),
                        eq(person.getFirstName()),
                        eq(person.getLastName()),
                        eq(person.getHeightInCm()),
                        eq(person.getDOB()),
                        eq(person.getAgeInMonths()),
                        eq(person.getCityOfBirth().getId()),
                        eq(person.getCityOfResidence().getId())
                );

        verify(jdbcTemplate,times(2))
                .queryForObject(
                        eq("SELECT * FROM select_city_by_pptbr(?)"),
                        eq(cityMapper),
                        any(Integer.class));
        verify(jdbcTemplate,times(1))
                .queryForObject(
                        eq("SELECT * FROM select_person_by_id(?)"),
                        eq(personMapper),
                        eq(person.getId()));
        verify(personValidator, times(1))
                .validateForSave(personSaveDTO);
        verify(personValidator, times(1))
                .validateUpdateId(personSaveDTO);
        verify(personSaveConverter, times(1))
                .toEntity(personSaveDTO);
        verify(personSaveConverter, times(1))
                .toDto(any(Person.class));
    }

    @Test
    @DisplayName("Get all Smederevo test")
    public void getAllSmederevoTest() throws SQLException {
        val city1 = City.builder().id(1L)
                .pptbr(11_300).name("Smederevo").build();
        val city2 = City.builder().id(2L)
                .pptbr(19_000).name("Zajecar").build();
        val person1 = new Person(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1), 0,
                city1,city2
        );
        val person2 = new Person(
                1L, "Sima", "Simic", 177,
                LocalDate.of(1999,2,2), 0,
                city1,city2
        );
        val personList = List.of(person1, person2);

        val cityDTO1 = new CityDTO(
                1L, "Smederevo"
        );
        val cityDto2 = new CityDTO(
                2L, "Zajecar"
        );
        val personDto1 = new PersonDisplayDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1), 0,
                "Smederevo", 11_300,
                "Zajecar", 19_000
        );
        val personDto2 = new PersonDisplayDTO(
                2L, "Sima", "Simic", 177,
                LocalDate.of(1999,2,2), 0,
                "Smederevo", 11_300,
                "Zajecar", 19_000
        );
        final List<PersonDisplayDTO> personDtoList
                = List.of(personDto1, personDto2);

        when(personDisplayConverter.listToDTO(personList))
                .thenReturn(personDtoList);
        when(jdbcTemplate.query(
                eq("SELECT * FROM select_smederevci()"),
                any(PersonRowMapper.class)
        )).thenReturn(personList);

        final List<PersonDisplayDTO> smederevci =
                personService.getAllSmederevci();

        assertThat(smederevci)
                .isEqualTo(personDtoList);
        verify(personDisplayConverter,times(1))
                .listToDTO(personList);
        verify(jdbcTemplate, times(1))
                .query(eq("SELECT * FROM select_smederevci()"),
                        eq(personMapper));
    }

    @Test
    @DisplayName("Get all adults test")
    public void getAllAdultsTest() throws SQLException {
        val city1 = City.builder().id(1L)
                .pptbr(11_300).name("Smederevo").build();
        val city2 = City.builder().id(2L)
                .pptbr(19_000).name("Zajecar").build();
        val person1 = new Person(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1), 300,
                city1,city2
        );
        val person2 = new Person(
                1L, "Sima", "Simic", 177,
                LocalDate.of(1999,2,2), 300,
                city1,city2
        );
        val personList = List.of(person1, person2);

        val cityDTO1 = new CityDTO(
                1L, "Smederevo"
        );
        val cityDto2 = new CityDTO(
                2L, "Zajecar"
        );
        val personDto1 = new PersonDisplayDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1), 300,
                "Smederevo", 11_300,
                "Zajecar", 19_000
        );
        val personDto2 = new PersonDisplayDTO(
                2L, "Sima", "Simic", 177,
                LocalDate.of(1999,2,2), 300,
                "Smederevo", 11_300,
                "Zajecar", 19_000
        );
        final List<PersonDisplayDTO> personDtoList
                = List.of(personDto1, personDto2);

        when(personDisplayConverter.listToDTO(personList))
                .thenReturn(personDtoList);
        when(jdbcTemplate.query(
                eq("SELECT * FROM select_adults()"),
                any(PersonRowMapper.class)
        )).thenReturn(personList);

        final List<PersonDisplayDTO> adults =
                personService.getAllAdults();

        assertThat(adults)
                .isEqualTo(personDtoList);
        verify(personDisplayConverter,times(1))
                .listToDTO(personList);
        verify(jdbcTemplate, times(1))
                .query(eq("SELECT * FROM select_adults()"),
                        eq(personMapper));
    }
}

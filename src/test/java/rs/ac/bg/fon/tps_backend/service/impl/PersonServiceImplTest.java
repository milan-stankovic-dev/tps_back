package rs.ac.bg.fon.tps_backend.service.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonDisplayConverter;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonSaveConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;
import rs.ac.bg.fon.tps_backend.exception.UnknownCityException;
import rs.ac.bg.fon.tps_backend.repository.CityRepository;
import rs.ac.bg.fon.tps_backend.repository.PersonRepository;
import rs.ac.bg.fon.tps_backend.validator.PersonValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class PersonServiceImplTest {
    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private CityRepository cityRepository;

    private final PersonServiceImpl personService;
    @MockBean
    private PersonSaveConverter personSaveConverter;
    @MockBean
    private PersonDisplayConverter personDisplayConverter;
    @MockBean
    private PersonValidator personValidator;
    @MockBean
    private PersonTemplateServiceImpl personTemplateService;

    @Autowired
    public PersonServiceImplTest(@Qualifier(value = "personServiceImpl")
                                     PersonServiceImpl personService) {
        this.personService = personService;
    }


    @Test
    @DisplayName("Select all persons empty")
    void selectAllPersonsEmpty() {
        val personList = personService.getAll();

        assertThat(personList).isEmpty();
        verify(personRepository,
                times(1)).findAll();
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
                .pptbr(11_000)
                .build();
        val cityResidence = City.builder()
                .pptbr(19_000)
                .build();

        final Person person = new Person(1L, "Pera", "Peric",
                189, LocalDate.of(2000,1,1),0,
                cityBirth,cityResidence);
        when(personValidator.setLastNameToJovanovicDefault(personSaveDTO))
                .thenReturn(personSaveDTO);

        when(cityRepository.findByPptbr(anyInt()))
                .thenAnswer(invocation -> {
                    if((int) invocation.getArgument(0) == 11_000){
                        return Optional.of(cityBirth);
                    } else {
                        return Optional.of(cityResidence);
                    }
                });
        when(personSaveConverter.toEntity(personSaveDTO))
                .thenReturn(person);
        when(personSaveConverter.toDto(person))
                .thenReturn(personSaveDTO);
        when(personRepository.save(person))
                .thenReturn(person);

        final PersonSaveDTO personSaved = personService.savePerson(personSaveDTO);
        assertThat(personSaved).isEqualTo(personSaveDTO);
        verify(personRepository,times(1))
                .save(person);
        verify(personValidator, times(1))
                .validateForSave(personSaveDTO);
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

        assertThatThrownBy(()-> personService.savePerson(person))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");
        verify(personRepository, never()).save(
                personSaveConverter.toEntity(person));
    }

    @Test
    @DisplayName("Throws exception due to unknown city of residence")
    void saveUnknownCityOfResidence() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1),
                19_000, -1
        );
        when(personValidator.setLastNameToJovanovicDefault(person))
                .thenReturn(person);

        when(cityRepository.findByPptbr(person.birthCityCode()))
                .thenReturn(Optional.of(new City()));

        assertThatThrownBy(()-> personService.savePerson(person))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");
        verify(personRepository, never()).save(
                personSaveConverter.toEntity(person));
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
        verify(personRepository, never()).save(
                personSaveConverter.toEntity(person));
    }


    @Test
    @DisplayName("Delete person null")
    void deleteNullPersonId() {
        assertThatThrownBy(()->personService.deletePerson(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Your id may not be null.");
        verify(personRepository, never()).deleteById(null);
    }

    @Test
    @DisplayName("Delete person non null")
    void deletePerson() throws Exception {
        when(personRepository.existsById(1L))
                .thenReturn(true);
        personService.deletePerson(1L);

        verify(personRepository, times(1))
                .deleteById(1L);
    }

    @Test
    @DisplayName("Delete non existing person fail")
    void deleteNonExistingPersonFailTest() {
        when(personRepository.existsById(1L))
                .thenReturn(false);
        assertThatThrownBy(() -> personService.deletePerson(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Person with said id does not exist.");
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

        verify(personRepository, never()).save(person);
        verify(personValidator, times(1))
                .validateUpdateId(personSaveDTO);
    }

    @Test
    @DisplayName("Throws exception after trying to update non existing person")
    void updateNonExistingPerson() throws Exception {
        final PersonSaveDTO personSaveDTO = new PersonSaveDTO(
                5555L, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );
        final Person person = new Person(
                null, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1),0,
                City.builder().pptbr(11_000).build(), City.builder().pptbr(19_0000).build()
        );
        when(personValidator.setLastNameToJovanovicDefault(personSaveDTO))
                .thenReturn(personSaveDTO);

        when(personSaveConverter.toEntity(personSaveDTO))
                .thenReturn(person);

        when(personRepository.findById(personSaveDTO.id()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.updatePerson(personSaveDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("The person with given ID may not exist.");

        verify(personRepository, never()).save(personSaveConverter
                .toEntity(personSaveDTO));
    }

    @Test
    @DisplayName("Throws exception due to unknown city of birth")
    void updateUnknownCityOfBirth() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );

        when(personValidator.setLastNameToJovanovicDefault(person))
                .thenReturn(person);

        when(personRepository.findById(person.id()))
                .thenReturn(Optional.of(new Person()));

        ArgumentCaptor<Person> personCaptor =
                ArgumentCaptor.forClass(Person.class);

        assertThatThrownBy(()->personService.updatePerson(person))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");

        verify(personRepository, never()).save(personCaptor.capture());
    }

    @Test
    @DisplayName("Throws exception due to unknown city of residence")
    void updateUnknownCityOfResidence() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );

        when(personValidator.setLastNameToJovanovicDefault(person))
                .thenReturn(person);
        when(personRepository.findById(person.id()))
                .thenReturn(Optional.of(new Person()));
        when(cityRepository.findByPptbr(11_000))
                .thenReturn(Optional.of(new City()));

        ArgumentCaptor<Person> personCaptor =
                ArgumentCaptor.forClass(Person.class);

        assertThatThrownBy(()->personService.updatePerson(person))
                .isInstanceOf(UnknownCityException.class)
                .hasMessage("Person contains reference to unknown city.");

        verify(personRepository, never()).save(personCaptor.capture());
    }

    @Test
    @DisplayName("Invalid person passed to update")
    void invalidPersonInputUpdateTest() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "P", "Peric",
                189,
                LocalDate.of(2000,1,1),
                -1, 11_000
        );
        doThrow(new PersonNotInitializedException("Person's name length is invalid."))
                .when(personValidator).validateForSave(person);

        assertThatThrownBy(()-> personService.updatePerson(person))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name length is invalid.");
        verify(personValidator, times(1))
                .validateForSave(person);
        verify(personRepository, never()).save(
                personSaveConverter.toEntity(person));
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
                .pptbr(11_000)
                .build();
        val cityResidence = City.builder()
                .pptbr(19_000)
                .build();

        final Person person = new Person(1L, "Pera", "Peric",
                189, LocalDate.of(2000,1,1),0,
                cityBirth,cityResidence);

        when(personValidator.setLastNameToJovanovicDefault(personSaveDTO))
                .thenReturn(personSaveDTO);

        when(cityRepository.findByPptbr(anyInt()))
                .thenAnswer(invocation -> {
                    if((int) invocation.getArgument(0) == 11_000){
                        return Optional.of(cityBirth);
                    } else {
                        return Optional.of(cityResidence);
                    }
                });
        when(personSaveConverter.toEntity(personSaveDTO))
                .thenReturn(person);
        when(personSaveConverter.toDto(person))
                .thenReturn(personSaveDTO);
        when(personRepository.save(person))
                .thenReturn(person);
        when(personRepository.findById(personSaveDTO.id()))
                .thenReturn(Optional.of(person));

        final PersonSaveDTO personSaved = personService.updatePerson(personSaveDTO);
        assertThat(personSaved).isEqualTo(personSaveDTO);

        verify(personRepository,times(1))
                .save(person);
        verify(personValidator, times(1))
                .validateForSave(personSaveDTO);
        verify(personValidator, times(1))
                .validateUpdateId(personSaveDTO);
    }

    @Test
    @DisplayName("Get all Smederevo test")
    public void getAllSmederevo() throws SQLException {
        when(personTemplateService.getAllSmederevci())
                .thenReturn(new ArrayList<>());

        val result = personService.getAllSmederevci();

        assertThat(result)
                .isEqualTo(new ArrayList<>());
        verify(personTemplateService, times(1))
                .getAllSmederevci();
    }

    @Test
    @DisplayName("Get all adults test")
    public void getAllAdults() throws SQLException {
        when(personTemplateService.getAllAdults())
                .thenReturn(new ArrayList<>());

        val result = personService.getAllAdults();

        assertThat(result)
                .isEqualTo(new ArrayList<>());
        verify(personTemplateService, times(1))
                .getAllAdults();
    }

    @Test
    @DisplayName("Get max height test")
    public void getMaxHeightTest() {
        final int expectedResult = 190;
        when(personRepository.getMaxHeight())
                .thenReturn(expectedResult);

        final int actualResult =
                personService.getMaxHeight();

        assertThat(actualResult)
                .isEqualTo(expectedResult);
        verify(personRepository,times(1))
                .getMaxHeight();
    }

    @Test
    @DisplayName("Get average age years test")
    public void getAverageAgeYearsTest() {
        final Double expectedResult = 25.50;
        when(personRepository.getAverageAgeYears())
                .thenReturn(expectedResult);

        final Double actualResult =
                personService.getAverageAgeYears();

        assertThat(actualResult)
                .isEqualTo(expectedResult);
        verify(personRepository,times(1))
                .getAverageAgeYears();
    }
}

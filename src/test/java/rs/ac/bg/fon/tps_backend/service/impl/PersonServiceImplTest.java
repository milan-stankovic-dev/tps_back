package rs.ac.bg.fon.tps_backend.service.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonDisplayConverter;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonSaveConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;
import rs.ac.bg.fon.tps_backend.exception.UnknownCityException;
import rs.ac.bg.fon.tps_backend.repository.CityRepository;
import rs.ac.bg.fon.tps_backend.repository.PersonRepository;
import rs.ac.bg.fon.tps_backend.service.PersonService;
import rs.ac.bg.fon.tps_backend.validator.PersonValidator;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@ExtendWith(MockitoExtension.class)
public class PersonServiceImplTest {
    @Mock
    private PersonRepository personRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private PersonService personService;
    @Mock
    private PersonSaveConverter personSaveConverter;
    @Mock
    private PersonDisplayConverter personDisplayConverter;
    @Mock
    private PersonValidator personValidator;


    @BeforeEach
    void setUp() {
        personService = new PersonServiceImpl(
                personRepository,
                cityRepository,
                personSaveConverter,
                personDisplayConverter,
                personValidator);
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
    @DisplayName("Person save verification works!")
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
        personService.deletePerson(1L);
        verify(personRepository, times(1))
                .deleteById(1L);
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
}

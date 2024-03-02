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
                .hasMessage("Not a valid city of birth.");
        verify(personRepository, never()).save(
                personSaveConverter.toEntity(person));
    }

    @Test
    @DisplayName("Throws exception due to unknown city of birth")
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
                .hasMessage("Not a valid city of residence.");
        verify(personRepository, never()).save(
                personSaveConverter.toEntity(person));
    }

    @Test
    @DisplayName("Saves normally.")
    void saveCityNormal() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000,1,1),
                19_000, 11_000
        );
        final Person personForReturn = new Person(
                1L, "Pera", "Peric",
                189,
                LocalDate.of(2000,1,1),
                0,
                City.builder()
                        .pptbr(19_000)
                        .build(),
                City.builder()
                        .pptbr(11_000)
                        .build());

        when(personSaveConverter.toEntity(person)).thenReturn(personForReturn);
        when(personSaveConverter.toDto(personForReturn)).thenReturn(person);
        when(cityRepository.findByPptbr(person.birthCityCode()))
                .thenReturn(Optional.of(City.builder().pptbr(19_000).build()));
        when(cityRepository.findByPptbr(person.residenceCityCode()))
                .thenReturn(Optional.of(City.builder().pptbr(11_000).build()));
        when(personRepository.save(personSaveConverter.toEntity(person)))
                .thenReturn(personForReturn);

        System.out.println("Before saving: " + person);

        final PersonSaveDTO personSaved = personService.savePerson(person);

        System.out.println("After saving: " + personSaved);

        verify(personRepository, times(1)).save(
                personSaveConverter.toEntity(person));
        assertThat(personSaved).isNotNull();
        assertThat(personSaved).isEqualTo(person);
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
    @Disabled
    @DisplayName("Throws exception due to null id for person to update")
    void updateNullIdPerson() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                null, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );

        ArgumentCaptor<Person> personCaptor =
                ArgumentCaptor.forClass(Person.class);

        assertThatThrownBy(()->personService.updatePerson(person))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Your person's ID may not be null.");

        verify(personRepository, never()).save(personCaptor.capture());
    }

    @Test
    @DisplayName("Throws exception after trying to update non existing person")
    void updateNonExistingPerson() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                5555L, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );

//        when(cityRepository.findByPtpbr(person.birthCityCode()))
//                .thenReturn(Optional.empty());

        ArgumentCaptor<Person> personCaptor =
                ArgumentCaptor.forClass(Person.class);

        assertThatThrownBy(()->personService.updatePerson(person))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("The person with given ID may not exist.");

        verify(personRepository, never()).save(personCaptor.capture());
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
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("The city of birth does not exist.");

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
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("The city of residence does not exist.");

        verify(personRepository, never()).save(personCaptor.capture());
    }

    @Test
    @DisplayName("Updates person successfully.")
    void updateSuccessful() throws Exception {
        final PersonSaveDTO person = new PersonSaveDTO(
                1L, "Pera", "Peric", 189,
                LocalDate.of(2000, 1, 1),
                11_000, 19_000
        );
//
//        when(personRepository.findById(person.id()))
//                .thenReturn(Optional.of(new Person()));
        when(cityRepository.findByPptbr(11_000))
                .thenReturn(Optional.of(City.builder()
                        .pptbr(11_000).build()));
        when(cityRepository.findByPptbr(19_000))
                .thenReturn(Optional.of(City.builder()
                        .pptbr(19_000).build()));
        when(personSaveConverter.toEntity(person))
                .thenReturn(
                    new Person(1L, "Pera", "Peric", 189,
                            LocalDate.of(2000,1,1),
                            0,
                            City.builder().pptbr(11_000).build(),
                            City.builder().pptbr(19_000).build())
                );

        ArgumentCaptor<Person> personCaptor =
                ArgumentCaptor.forClass(Person.class);

        personService.savePerson(person);
//        final Person personCaptured = personCaptor.capture();

        verify(personRepository, times(1))
                .save(personCaptor.capture());

//        assertThat(personCaptured).isEqualTo(
//                personSaveConverter.toEntity(person));

    }
}

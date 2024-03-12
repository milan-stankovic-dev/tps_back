package rs.ac.bg.fon.tps_backend.converter.impl;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PersonSaveConverterTest {
    private final PersonSaveConverter converter;

    @Autowired
    PersonSaveConverterTest(PersonSaveConverter converter) {
        this.converter = converter;
    }

    @Test
    @DisplayName("To entity null")
    void toEntityNullTest() {
        final Person personExpected = null;
        final PersonSaveDTO personInput = null;

        final Person personActual =
                converter.toEntity(personInput);

        assertThat(personExpected)
                .isEqualTo(personActual);
    }

    @Test
    @DisplayName("To entity normal")
    void toEntityNormalTest() {
        final Person personExpected = new Person(1L,
                "Pera", "Peric",
                190,
                LocalDate.of(2000,1,1),
                0,
                City.builder()
                        .pptbr(11_000)
                        .build(),
                City.builder()
                        .pptbr(11_000)
                        .build());

        final PersonSaveDTO personInput = new PersonSaveDTO(
                1L, "Pera", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000
        );

        final Person personActual =
                converter.toEntity(personInput);

        assertThat(personExpected).isEqualTo(personActual);
    }

    @Test
    @DisplayName("To dto null")
    void toDtoNullTest() {
        final PersonSaveDTO personExpected = null;
        final Person personInput = null;

        final PersonSaveDTO personActual =
                converter.toDto(personInput);

        assertThat(personExpected)
                .isEqualTo(personActual);
    }

    @Test
    @DisplayName("To dto normal")
    void toDtoNormalTest() {
        final PersonSaveDTO personExpected = new PersonSaveDTO(
                1L, "Pera", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000
        );

        final Person personInput = new Person(1L,
                "Pera", "Peric",
                190,
                LocalDate.of(2000,1,1),
                0,
                City.builder()
                        .pptbr(11_000)
                        .build(),
                City.builder()
                        .pptbr(11_000)
                        .build());

        final PersonSaveDTO personActual =
                converter.toDto(personInput);

        assertThat(personExpected).isEqualTo(personActual);
    }

    @Test
    @DisplayName("To dto null cities")
    void toDtoNullCities() {
        final PersonSaveDTO personExpected = new PersonSaveDTO(
                1L, "Pera", "Peric",
                190, LocalDate.of(2000,1,1),
                0, 0
        );

        final Person personInput = new Person(1L,
                "Pera", "Peric",
                190,
                LocalDate.of(2000,1,1),
                0,
                null, null );

        final PersonSaveDTO personActual =
                converter.toDto(personInput);

        assertThat(personExpected).isEqualTo(personActual);
    }


}

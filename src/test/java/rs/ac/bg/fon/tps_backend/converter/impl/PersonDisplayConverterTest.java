package rs.ac.bg.fon.tps_backend.converter.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PersonDisplayConverterTest {
    private final PersonDisplayConverter converter;

    @Autowired
    public PersonDisplayConverterTest(PersonDisplayConverter converter) {
        this.converter = converter;
    }

    @Test
    @DisplayName("To entity null")
    void toEntityNullTest() {
        final PersonDisplayDTO personInput = null;
        final Person personExpected = null;
        final Person personActual = converter.toEntity(personInput);

        assertThat(personExpected)
                .isEqualTo(personActual);
    }

    @Test
    @DisplayName("To entity normal")
    void toEntityNormalTest() {
        final PersonDisplayDTO personInput = new PersonDisplayDTO(
            1L, "Pera", "Peric", 190,
                LocalDate.of(2000,1,1),
                0, "Beograd", 11_000,
                "Beograd", 11_000
        );

        final Person personExpected = new Person(1L,
                "Pera", "Peric",
                190,
                LocalDate.of(2000,1,1),
                0,
                City.builder()
                        .name("Beograd")
                        .pptbr(11_000)
                        .build(),
                City.builder()
                        .name("Beograd")
                        .pptbr(11_000)
                        .build());
        final Person personActual = converter.toEntity(personInput);

        assertThat(personExpected)
                .isEqualTo(personActual);
    }

    @Test
    @DisplayName("To dto null")
    void toDtoNullTest() {
        final Person personInput = null;
        final PersonDisplayDTO personExpected = null;
        final PersonDisplayDTO personActual = converter.toDto(personInput);

        assertThat(personExpected)
                .isEqualTo(personActual);
    }

    @Test
    @DisplayName("To dto normal")
    void toDtoNormalTest() {
        final Person personInput = new Person(1L,
                "Pera", "Peric",
                190,
                LocalDate.of(2000,1,1),
                0,
                City.builder()
                        .name("Beograd")
                        .pptbr(11_000)
                        .build(),
                City.builder()
                        .name("Beograd")
                        .pptbr(11_000)
                        .build());

        final PersonDisplayDTO personExpected =
                new PersonDisplayDTO(
                        1L, "Pera", "Peric", 190,
                        LocalDate.of(2000,1,1),
                        0, "Beograd", 11_000,
                        "Beograd", 11_000
                );
        final PersonDisplayDTO personActual = converter.toDto(personInput);

        assertThat(personExpected)
                .isEqualTo(personActual);
    }

    @Test
    @DisplayName("To dto null cities")
    void toDtoNullCitiesTest() {
        final Person personInput = new Person(1L,
                "Pera", "Peric",
                190,
                LocalDate.of(2000,1,1),
                0,
                null, null);

        final PersonDisplayDTO personExpected =
                new PersonDisplayDTO(
                        1L, "Pera", "Peric", 190,
                        LocalDate.of(2000,1,1),
                        0, null, 0,
                        null, 0
                );
        final PersonDisplayDTO personActual = converter.toDto(personInput);

        assertThat(personExpected)
                .isEqualTo(personActual);
    }
}

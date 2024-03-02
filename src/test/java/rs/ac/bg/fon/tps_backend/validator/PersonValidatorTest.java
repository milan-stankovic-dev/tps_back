package rs.ac.bg.fon.tps_backend.validator;

import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import rs.ac.bg.fon.tps_backend.constraints.PersonConstraints;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static rs.ac.bg.fon.tps_backend.validator.PersonValidator.validateForSave;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static rs.ac.bg.fon.tps_backend.util.DateConverterUtil.stringToLocalDate;

public class PersonValidatorTest {
    private final PersonConstraints constraints = new PersonConstraints(
            2,30,2,30,
            70, 260, LocalDate.of(1950, 1,1),
            LocalDate.of(2006,1,1));

    @Test
    @DisplayName("Person null validation test")
    public void personIsNullTest() {
        assertThatThrownBy(()->validateForSave(null))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Your person may not be null.");
    }

    @Test
    @DisplayName("Person first name null validation test")
    public void personsNameIsNullTest() {
        val person = new PersonSaveDTO(1L, null, "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Your person may not contain malformed fields.");
    }

    @Test
    @DisplayName("Person last name null validation test")
    public void personsLastNameIsNullTest() {
        val person = new PersonSaveDTO(1L, "Pera", null,
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Your person may not contain malformed fields.");
    }

    @Test
    @DisplayName("Person's name lowercase validation test")
    public void personsNameLowercaseTest() {
        val person = new PersonSaveDTO(1L, "pera", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name or last name may not start with a lowercase letter.");
    }

    @Test
    @DisplayName("Person's name blank space validation test")
    public void personsNameBlankSpaceTest() {
        val person = new PersonSaveDTO(1L, "Pe ra", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name contains illegal characters.");
    }

    @Test
    @DisplayName("Person's name number validation test")
    public void personsNameNumberTest() {
        val person = new PersonSaveDTO(1L, "Pe7ra", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name contains illegal characters.");
    }

    @Test
    @DisplayName("Person's name number validation test")
    public void personsNameUpperCaseTest() {
        val person = new PersonSaveDTO(1L, "PeRa", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name contains uppercase letters.");
    }

    @Test
    @DisplayName("Person's last name lowercase validation test")
    public void personsLastNameLowercase() {
        val person = new PersonSaveDTO(1L, "Pera", "peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name or last name may not start with a lowercase letter.");
    }

    @Test
    @DisplayName("Person's name blank space validation test")
    public void personsLastNameBlankSpaceTest() {
        val person = new PersonSaveDTO(1L, "Pera", "P eric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name contains illegal characters.");
    }

    @Test
    @DisplayName("Person's name number validation test")
    public void personsLastNameNumberTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Per6ic",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name contains illegal characters.");
    }

    @Test
    @DisplayName("Person's name number validation test")
    public void personsLasNameUppercaseTest() {
        val person = new PersonSaveDTO(1L, "Pera", "PerIc",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(()->validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name contains uppercase letters.");
    }

    @Test
    @DisplayName("Constraints are null test")
    public void constraintsNullTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Constraints must be given for checking the person object.");
    }

    @Test
    @DisplayName("Name too short test")
    public void nameTooShortTest() {
        val person = new PersonSaveDTO(1L, "P", "Peric",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name length is invalid.");
    }

    @Test
    @DisplayName("Last name too short test")
    public void lastNameTooShortTest() {
        val person = new PersonSaveDTO(1L, "Pera", "P",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's last name length is invalid.");
    }

    @Test
    @DisplayName("Name too short test")
    public void nameTooLongTest() {
        val person = new PersonSaveDTO(1L, "Peraaaaaaaaaaaaaaaaaaaaaaaaaaaa", "P",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's name length is invalid.");
    }

    @Test
    @DisplayName("Last name too long test")
    public void lastNameTooLongTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Periccccccccccccccccccccccccccc",
                190, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's last name length is invalid.");
    }

    @Test
    @DisplayName("Height too small test")
    public void heightTooSmallTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Peric",
                60, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's height is invalid.");
    }

    @Test
    @DisplayName("Height too big test")
    public void heightTooBigTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Peric",
                300, LocalDate.of(2000,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's height is invalid.");
    }

    @Test
    @DisplayName("Dob too early test")
    public void dobTooEarlyTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Peric",
                190, LocalDate.of(1900,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's date of birth is invalid.");
    }

    @Test
    @DisplayName("Dob too late test")
    public void dobTooLateTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Peric",
                190, LocalDate.of(2100,1,1),
                11_000, 11_000);
        assertThatThrownBy(() -> validateForSave(person, constraints))
                .isInstanceOf(PersonNotInitializedException.class)
                .hasMessage("Person's date of birth is invalid.");
    }

    @ParameterizedTest
    @CsvSource({
            "1,'Sima','Simić',190,'1999-01-02',11000,19000",
            "2,'Sara','Marković',165,'1997-04-20',19000,18000",
            "3,'Zoran','Jejić',170,'1960-10-23',32000,36000",
            "4,'Marija','Štavljanin',169,'1998-05-10',31000,11000"
    })
    @DisplayName("Normal insert test {index}")
    public void normalInsertsCSV(@AggregateWith(PersonAggregator.class) PersonSaveDTO person) {
        assertDoesNotThrow(()->{
           validateForSave(person, constraints);
        });
    }

    @Test
    @Disabled
    @DisplayName("Testing the validity of the same named method with different signature.")
    public void fewerArgsMethodTest() {
        val person = new PersonSaveDTO(1L, "Pera", "Peric",
                190, LocalDate.of(1990,1,1),
                11_000, 11_000);
        mockStatic(PersonValidator.class);

        validateForSave(person);

        verifyStatic(PersonValidator.class,times(1));
        validateForSave(eq(person), any());

        verifyNoMoreInteractions(PersonValidator.class);
    }

    private static class PersonAggregator implements ArgumentsAggregator{

        @Override
        public Object aggregateArguments(ArgumentsAccessor acc, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new PersonSaveDTO(acc.getLong(0), acc.getString(1), acc.getString(2),
                    acc.getInteger(3), stringToLocalDate(acc.getString(4)),
                    acc.getInteger(5), acc.getInteger(6));
        }
    }
}

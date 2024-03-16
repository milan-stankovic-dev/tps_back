package rs.ac.bg.fon.tps_backend.util;

import lombok.SneakyThrows;
import lombok.val;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.ac.bg.fon.tps_backend.constants.DateConstant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DateConverterUtilTest {
    private final DateConverterUtil converter;
    private final DateConstant dateConstant;
    private final DateFormat formatterForDate;
    private final DateTimeFormatter formatterForLocalDate;

    @Autowired
    DateConverterUtilTest(
            DateConverterUtil converter,
            DateConstant dateConstant
    ) {
        this.converter = converter;
        this.dateConstant = dateConstant;
        this.formatterForDate = new SimpleDateFormat(dateConstant.getDATE_FORMAT());
        this.formatterForLocalDate = DateTimeFormatter.ofPattern(dateConstant.getDATE_FORMAT());
    }

    @Test
    @DisplayName("Null sql date to localDate")
    void nullSQLDateToLocalDateTest() {
        assertThatThrownBy(() -> converter.sqlDateToLocalDate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date may not be null.");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/date_mock.csv")
    @DisplayName("Normal sql date to localDate")
    void normalSQLDateToLocalDateTest(String dateString) {
        val localDateInput = formatterForLocalDate.parse(dateString, LocalDate::from);

        val localDateConverted = converter.sqlDateToLocalDate(java.sql.Date.valueOf(dateString));
        val localDateExpected = localDateInput;

        assertThat(localDateConverted)
                .isEqualTo(localDateExpected);
    }

    @Test
    @DisplayName("Null local date to date")
    void nullLocalDateToDateTest() {
        assertThatThrownBy(() -> converter.localDateToDate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date may not be null.");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/date_mock.csv")
    @DisplayName("Normal localDate to date")
    @SneakyThrows(ParseException.class)
    void normalToLocalDateToDateTest(String dateString) {
        val localDateInput = formatterForDate.parse(dateString);

        final List<Integer> dateNumbers = Arrays.stream(dateString.split("-")).map(
                Integer::parseInt).toList();

        val dateConverted = converter.localDateToDate(LocalDate.of(
                dateNumbers.get(0), dateNumbers.get(1), dateNumbers.get(2)
        ));
        val dateExpected = localDateInput;

        assertThat(dateConverted)
                .isEqualTo(dateExpected);
    }

    @Test
    @DisplayName("Null util date to localDate")
    void nullUtilDateToLocalDateTest() {
        assertThatThrownBy(() -> converter.utilDateToLocalDate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date may not be null.");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/date_mock.csv")
    @DisplayName("Normal sql date to localDate")
    @SneakyThrows(ParseException.class)
    void normalUtilDateToLocalDateTest(String dateString)  {
        val dateInput = formatterForDate.parse(dateString);
        val localDateInput = formatterForLocalDate.parse(dateString, LocalDate::from);

        val localDateConverted = converter.utilDateToLocalDate(dateInput);
        val localDateExpected = localDateInput;

        assertThat(localDateConverted)
                .isEqualTo(localDateExpected);
    }

}

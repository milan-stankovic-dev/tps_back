package rs.ac.bg.fon.tps_backend.util;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import rs.ac.bg.fon.tps_backend.constants.DateConstant;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class DateConverterUtilTest {
    private static DateConverterUtil converter;
    private static DateConstant dateConstant;
    private final DateFormat formatterForDate =
            new SimpleDateFormat(dateConstant.getDATE_FORMAT());
    private final DateTimeFormatter formatterForLocalDate =
            DateTimeFormatter.ofPattern(dateConstant.getDATE_FORMAT());

    @BeforeAll
    static void beforeAll() {
        converter = new DateConverterUtil();
        try {
            dateConstant = new DateConstant(new PropertyUtil());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        converter = null;
    }

    @Test
    @DisplayName("Null sql date to localDate")
    public void nullSQLDateToLocalDateTest() {
        assertThatThrownBy(() -> converter.sqlDateToLocalDate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date may not be null.");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/date_mock.csv")
    @DisplayName("Normal sql date to localDate")
    public void normalSQLDateToLocalDateTest(String dateString) {
        val localDateInput = formatterForLocalDate.parse(dateString, LocalDate::from);

        val localDateConverted = converter.sqlDateToLocalDate(java.sql.Date.valueOf(dateString));
        val localDateExpected = localDateInput;

        assertThat(localDateConverted)
                .isEqualTo(localDateExpected);
    }

    @Test
    @DisplayName("Null local date to date")
    public void nullLocalDateToDateTest() {
        assertThatThrownBy(() -> converter.localDateToDate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date may not be null.");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/date_mock.csv")
    @DisplayName("Normal localDate to date")
    public void normalToLocalDateToDateTest(String dateString) {
        val localDateInput = formatterForLocalDate.parse(dateString, LocalDate::from);

        val dateConverted = converter.sqlDateToLocalDate(java.sql.Date.valueOf(dateString));
        val dateExpected = localDateInput;

        assertThat(dateConverted)
                .isEqualTo(dateExpected);
    }

    @Test
    @DisplayName("Null util date to localDate")
    public void nullUtilDateToLocalDateTest() {
        assertThatThrownBy(() -> converter.utilDateToLocalDate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Date may not be null.");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/date_mock.csv")
    @DisplayName("Normal sql date to localDate")
    @SneakyThrows(ParseException.class)
    public void normalUtilDateToLocalDateTest(String dateString)  {
        val dateInput = formatterForDate.parse(dateString);
        val localDateInput = formatterForLocalDate.parse(dateString, LocalDate::from);

        val localDateConverted = converter.utilDateToLocalDate(dateInput);
        val localDateExpected = localDateInput;

        assertThat(localDateConverted)
                .isEqualTo(localDateExpected);
    }

}

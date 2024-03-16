package rs.ac.bg.fon.tps_backend.mapper;

import lombok.val;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import rs.ac.bg.fon.tps_backend.constants.DateConstant;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.util.DateConverterUtil;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PersonRowMappingTest {

    private final PersonRowMapper mapper;

    private final DateConstant dateConstant;
    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private ResultSet rs;

    @MockBean
    private CityRowMapper cityRowMapper;

    @MockBean
    private DateConverterUtil dateUtil;

    @Autowired
    public PersonRowMappingTest(PersonRowMapper mapper, DateConstant dateConstant) {
        this.mapper = mapper;
        this.dateConstant = dateConstant;
    }


    @Test
    @DisplayName("Null result set mapping")
    public void nullResultSetMappingTest() throws SQLException {
        assertThatThrownBy(()->mapper.mapRow(null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Result set may not be null.");
    }

    @Test
    @DisplayName("Empty result set mapping")
    public void emptyResultSetMappingTest() throws SQLException {
        when(rs.getLong("id")).thenReturn(0L);

        assertThat(mapper.mapRow(rs, 0))
                .isNull();
    }

    @Test
    @DisplayName("Regular mapping of result set")
    public void normalResultSetMappingTest() throws SQLException, ParseException {
        val expectedPersonCityResidence =  new City(2L,
                19210, "Bor", 15000);
        val expectedPersonCityBirth = new City(1L, 11000,
                "Beograd", 1370000);
        val expectedPerson = new Person(1L,"Pera","Peric",
                189,LocalDate.of(2000,1,1),
                0, expectedPersonCityBirth, expectedPersonCityResidence);

        when(jdbcTemplate.queryForObject(
                eq("SELECT * FROM select_city_by_id(?)"),
                eq(cityRowMapper),
                any(Long.class)))
                .thenAnswer(invocation -> {
                    final long cityId = invocation.getArgument(2);
                    return switch ((int)cityId){
                        case 1 ->
                             new City(1L, 11000,
                                    "Beograd", 1370000);
                        case 2 ->
                             new City(2L,19210, "Bor",15000);
                        default ->
                                throw new IllegalArgumentException("Unknown city id " + cityId);
                    };
                });

        when(rs.getLong(any(String.class)))
                .thenAnswer(invocation -> {
                   final String arg = invocation.getArgument(0);

                   return switch (arg){
                       case "id", "city_birth_id" -> 1L;
                       case "city_residence_id" -> 2L;
                       default ->
                           throw new IllegalArgumentException("Unknown argument passed to thenAnswer.");
                   };
                });

        when(rs.getString(any(String.class)))
                .thenAnswer(invocation -> {
                   final String argument = invocation.getArgument(0);
                   return switch (argument) {
                       case "first_name" -> "Pera";
                       case "last_name" -> "Peric";
                       default ->
                           throw new IllegalArgumentException("Unknown argument passed to thenAnswer.");
                   };
                });
        when(rs.getInt(any(String.class)))
                .thenAnswer(invocation -> {
                    final String argument = invocation.getArgument(0);
                    return switch (argument) {
                        case "height_in_cm" -> 189;
                        case "age_in_months" -> 0;
                        default ->
                                throw new IllegalArgumentException("Unknown argument passed to thenAnswer.");
                    };
                });
        when(rs.getDate("dob"))
                .thenReturn(new Date(
                        new SimpleDateFormat(dateConstant.getDATE_FORMAT())
                                .parse("2000-01-01").getTime()));

        when(dateUtil.sqlDateToLocalDate(any(Date.class)))
                .thenReturn(LocalDate.of(2000,1,1));

        assertThat(mapper.mapRow(rs, 0))
                .isEqualTo(expectedPerson);
    }

}

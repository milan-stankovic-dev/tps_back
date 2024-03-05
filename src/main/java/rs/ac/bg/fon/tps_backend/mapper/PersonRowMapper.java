package rs.ac.bg.fon.tps_backend.mapper;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.util.DateConverterUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class PersonRowMapper implements RowMapper<Person> {
private final JdbcTemplate jdbcTemplate;
private final CityRowMapper cityRowMapper;
private final DateConverterUtil dateConverter;
    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(rs == null) {
            throw new IllegalArgumentException("Result set may not be null.");
        }

        val cityOfBirth = jdbcTemplate.queryForObject(
                "SELECT * FROM select_city_by_id(?)",
                    cityRowMapper,
                    rs.getLong("city_birth_id")
        );
        val cityOfResidence = jdbcTemplate.queryForObject(
                "SELECT * FROM select_city_by_id(?)",
                cityRowMapper,
                rs.getLong("city_residence_id")
        );


        return Person.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .heightInCm(rs.getInt("height_in_cm"))
                .dOB(dateConverter.sqlDateToLocalDate(
                        rs.getDate("dob")))
                .ageInMonths(rs.getInt("age_in_months"))
                .cityOfBirth(cityOfBirth)
                .cityOfResidence(cityOfResidence)
                .build();
    }
}

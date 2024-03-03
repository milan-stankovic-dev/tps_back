package rs.ac.bg.fon.tps_backend.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.domain.City;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CityRowMapper implements RowMapper<City> {
    @Override
    public City mapRow(ResultSet rs, int rowNum) throws SQLException {
        return City.builder()
                .id(rs.getLong("id"))
                .pptbr(rs.getInt("pptbr"))
                .name(rs.getString("name"))
                .population(rs.getInt("population"))
                .build();
    }
}

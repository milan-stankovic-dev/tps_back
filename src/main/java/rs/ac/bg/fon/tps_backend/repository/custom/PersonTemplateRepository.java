package rs.ac.bg.fon.tps_backend.repository.custom;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.repository.CityRepository;
import rs.ac.bg.fon.tps_backend.repository.custom.DatabaseConnector;
import rs.ac.bg.fon.tps_backend.util.DateConverterUtil;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonTemplateRepository {
    private final DateConverterUtil dateUtil;
    private final CityRepository cityRepository;
    private final DatabaseConnector databaseConnector;
    public Optional<Person> getPersonById(Long id, String sqlQuery)
            throws SQLException, IOException, ParseException {
        if(id == null){
            throw new IllegalArgumentException("Id may not be null.");
        }
        if(sqlQuery == null || sqlQuery.isEmpty()){
            throw new IllegalArgumentException("Query may not be null or empty.");
        }

        @Cleanup
        val conn = databaseConnector.getConnection(
                "application.properties");
        final CallableStatement cs = conn.prepareCall(sqlQuery);
            cs.setLong(1, id);
            cs.registerOutParameter(2, Types.STRUCT);
            cs.setObject(2, null);
            cs.execute();

        val result = cs.getObject(2);

        if(result == null) {
            return Optional.empty();
        }

        return Optional.of(resultToPerson(result));
    }

    private Person resultToPerson(Object result)
                            throws ParseException {
        if(result == null){
            return null;
        }

        final String resultToString = result.toString();

        val tokens = resultToString.split(",");
        final Long id = Long.parseLong(tokens[0].substring(1));
        final String firstName = tokens[1];
        final String lastName = tokens[2];
        final int heightInCm = Integer.parseInt(tokens[3]);
        final LocalDate dob = dateUtil.utilDateToLocalDate(
                new SimpleDateFormat("yyyy-MM-dd").parse(tokens[4]));
        final int ageInMonths = Integer.parseInt(tokens[5]);
        final City cityOfBirth = cityRepository.findById(
            Long.parseLong(tokens[6])).get();
        final String purifiedStringResidenceId = tokens[7].substring(0,
                tokens[7].length() - 1);
        final City cityOfResidence = cityRepository.findById(
            Long.parseLong(purifiedStringResidenceId)
        ).get();

        return new Person(id, firstName, lastName,
                heightInCm, dob, ageInMonths, cityOfBirth, cityOfResidence);
    }
}

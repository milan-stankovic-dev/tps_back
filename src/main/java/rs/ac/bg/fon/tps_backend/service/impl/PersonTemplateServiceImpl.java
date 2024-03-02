package rs.ac.bg.fon.tps_backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonDisplayConverter;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonSaveConverter;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.UnknownCityException;
import rs.ac.bg.fon.tps_backend.lambda.UpdateQuery;
import rs.ac.bg.fon.tps_backend.mapper.PersonRowMapper;
import rs.ac.bg.fon.tps_backend.service.PersonService;
import rs.ac.bg.fon.tps_backend.validator.CityRowMapper;
import rs.ac.bg.fon.tps_backend.validator.PersonValidator;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PersonTemplateServiceImpl implements PersonService {
    private final JdbcTemplate jdbcTemplate;
    private final PersonSaveConverter personSaveConverter;
    private final PersonDisplayConverter personDisplayConverter;
    private final PersonRowMapper personRowMapper;
    private final CityRowMapper cityRowMapper;
    private final PersonValidator personValidator;

    @Override
    public List<PersonDisplayDTO> getAll() {
        return personDisplayConverter.listToDTO(
            jdbcTemplate.query("SELECT * FROM select_persons()",
                    personRowMapper)
        );
    }

    private PersonSaveDTO savePerson(PersonSaveDTO p,
                                     UpdateQuery updateQuery) throws Exception{
        personValidator.validateForSave(p);

        val cityOfBirthDB =
                jdbcTemplate.queryForObject("SELECT * FROM select_city_by_pptbr(?)",
                        cityRowMapper, p.birthCityCode());

        if(cityOfBirthDB == null) {
            throw new UnknownCityException("Not a valid city of birth.");
        }

        val cityOfResidenceDB =
                jdbcTemplate.queryForObject("SELECT * FROM select_city_by_pptbr(?)",
                        cityRowMapper, p.residenceCityCode());

        if(cityOfResidenceDB == null) {
            throw new UnknownCityException("Not a valid city of residence.");
        }

        val personToSave = personSaveConverter.toEntity(p);
        personToSave.setCityOfBirth(cityOfBirthDB);
        personToSave.setCityOfResidence(cityOfResidenceDB);

        updateQuery.updateQuery(personToSave);

        return personSaveConverter.toDto(
                personToSave
        );
    }

    @Override
    public PersonSaveDTO savePerson(PersonSaveDTO p) throws Exception {
        return savePerson(p,
                (person) -> {
                    jdbcTemplate.update("CALL insert_person(?, ?, ?, ?, ?, ?, ?)",
                            person.getFirstName(),
                            person.getLastName(),
                            person.getHeightInCm(),
                            person.getDOB(),
                            person.getAgeInMonths(),
                            person.getCityOfBirth().getId(),
                            person.getCityOfResidence().getId());
        } );
    }

    @Override
    public void deletePerson(Long id) throws Exception {
        jdbcTemplate.update("CALL delete_person(?)", id);
    }

    @Override
    public PersonSaveDTO updatePerson(PersonSaveDTO p) throws Exception {
        return savePerson(p,
                (person) -> {
                    jdbcTemplate.update("CALL update_person(?, ?, ?, ?, ?, ?, ?, ?)",
                            person.getId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getHeightInCm(),
                            person.getDOB(),
                            person.getAgeInMonths(),
                            person.getCityOfBirth().getId(),
                            person.getCityOfResidence().getId());
                } );

    }

    public PersonDisplayDTO getPersonById(Long id) throws Exception {
        val personDB = jdbcTemplate.queryForObject(
                "SELECT * FROM select_person_by_id(?)",
                 personRowMapper, id);

        if(personDB == null){
            throw new EntityNotFoundException("Person with given id does not exist");
        }

        return personDisplayConverter.toDto(
                personDB
        );
    }
}

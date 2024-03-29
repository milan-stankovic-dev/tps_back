package rs.ac.bg.fon.tps_backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonDisplayConverter;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonSaveConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.UnknownCityException;
import rs.ac.bg.fon.tps_backend.lambda.UpdateQuery;
import rs.ac.bg.fon.tps_backend.mapper.PersonRowMapper;
import rs.ac.bg.fon.tps_backend.repository.custom.PersonTemplateRepository;
import rs.ac.bg.fon.tps_backend.service.PersonService;
import rs.ac.bg.fon.tps_backend.mapper.CityRowMapper;
import rs.ac.bg.fon.tps_backend.validator.PersonValidator;

import java.util.List;

@Service("personTemplateServiceImpl")
@RequiredArgsConstructor
public class PersonTemplateServiceImpl implements PersonService {
    private final JdbcTemplate jdbcTemplate;
    private final PersonSaveConverter personSaveConverter;
    private final PersonDisplayConverter personDisplayConverter;
    private final PersonRowMapper personRowMapper;
    private final CityRowMapper cityRowMapper;
    private final PersonValidator personValidator;
    private final PersonTemplateRepository personTemplateRepository;

    @Override
    public List<PersonDisplayDTO> getAll() {
        return personDisplayConverter.listToDTO(
            jdbcTemplate.query("SELECT * FROM select_persons()",
                    personRowMapper)
        );
    }

    private City fetchCityIfPossible(int cityCode) {
        val cityDB =
                jdbcTemplate.queryForObject("SELECT * FROM select_city_by_pptbr(?)",
                        cityRowMapper, cityCode);

        if(cityDB == null) {
            throw new UnknownCityException("Person contains reference to unknown city.");
        }

        return cityDB;
    }

    private PersonSaveDTO savePerson(PersonSaveDTO p,
                                     UpdateQuery updateQuery) throws Exception{
        personValidator.validateForSave(p);
        val pFixed = personValidator.setLastNameToJovanovicDefault(p);


        val cityOfBirthDB = fetchCityIfPossible(pFixed.birthCityCode());
        val cityOfResidenceDB = fetchCityIfPossible(pFixed.residenceCityCode());
        val personToSave = personSaveConverter.toEntity(pFixed);

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
        if(id == null){
            throw new NullPointerException("Your id may not be null.");
        }

        getPersonById(id);

        jdbcTemplate.update("CALL delete_person(?)", id);
    }

    @Override
    public PersonSaveDTO updatePerson(PersonSaveDTO p) throws Exception {
        if(p != null){
            personValidator.validateUpdateId(p);
            if(p.id() != null){
              getPersonById(p.id());
            }
        }
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

    public Person getPersonById(Long id) throws Exception {
       val personDbOpt = personTemplateRepository.getPersonById(id,
                "CALL select_person_by_id(?,?)");

       if(personDbOpt.isEmpty()){
           throw new EntityNotFoundException("Person with given id does not exist");
       }

       return personDbOpt.get();
    }



    public List<PersonDisplayDTO> getAllSmederevci() {
        return personDisplayConverter.listToDTO(
                jdbcTemplate.query("SELECT * FROM select_smederevci()",
                        personRowMapper)
        );
    }

    @Override
    public List<PersonDisplayDTO> getAllAdults() {
        return personDisplayConverter.listToDTO(
                jdbcTemplate.query("SELECT * FROM select_adults()",
                        personRowMapper)
        );
    }

    @Override
    public Integer getMaxHeight() {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM max_height()", Integer.class);
    }

    @Override
    public Double getAverageAgeYears() {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM average_age_years()", Double.class);
    }
}

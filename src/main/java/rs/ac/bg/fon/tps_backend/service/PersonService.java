package rs.ac.bg.fon.tps_backend.service;

import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<PersonDisplayDTO> getAll();
    PersonSaveDTO savePerson(PersonSaveDTO p) throws Exception;
    void deletePerson(Long id) throws Exception;
    PersonSaveDTO updatePerson(PersonSaveDTO p) throws Exception;

    List<PersonDisplayDTO> getAllSmederevci() throws SQLException;

    List<PersonDisplayDTO> getAllAdults() throws SQLException;

    Integer getMaxHeight();

    Double getAverageAgeYears();
}

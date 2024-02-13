package rs.ac.bg.fon.tps_backend.service;

import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDTO;

import java.util.List;

public interface PersonService {
    List<PersonDTO> getAll();
    Person savePerson(PersonDTO p) throws Exception;
    Person deletePerson(Long id) throws Exception;
    Person updatePerson(PersonDTO p) throws Exception;
}

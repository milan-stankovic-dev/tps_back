package rs.ac.bg.fon.tps_backend.service.impl;

import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonConverter;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDTO;
import rs.ac.bg.fon.tps_backend.repository.PersonRepository;
import rs.ac.bg.fon.tps_backend.service.PersonService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final PersonConverter personConverter;

    @Override
    public List<PersonDTO> getAll() {
        return personConverter.listToDTO(
            personRepository.findAll()
        );
    }

    @Override
    public Person savePerson(PersonDTO p) throws Exception {
        return null;
    }

    @Override
    public Person deletePerson(Long id) throws Exception {
        return null;
    }

    @Override
    public Person updatePerson(PersonDTO p) throws Exception {
        return null;
    }
}

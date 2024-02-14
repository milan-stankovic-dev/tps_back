package rs.ac.bg.fon.tps_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonDisplayConverter;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonSaveConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;
import rs.ac.bg.fon.tps_backend.exception.UnknownCityException;
import rs.ac.bg.fon.tps_backend.repository.CityRepository;
import rs.ac.bg.fon.tps_backend.repository.PersonRepository;
import rs.ac.bg.fon.tps_backend.service.PersonService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final CityRepository cityRepository;
    private final PersonSaveConverter personSaveConverter;
    private final PersonDisplayConverter personDisplayConverter;

    @Override
    public List<PersonDisplayDTO> getAll() {
        return personDisplayConverter.listToDTO(
            personRepository.findAll()
        );
    }

    @Override
    public PersonSaveDTO savePerson(PersonSaveDTO p) throws Exception {
        if(p == null){
            throw new PersonNotInitializedException("Your person may not be null.");
        }

        if(p.firstName() == null || p.lastName() == null ||
            p.dOB() == null){
            throw new PersonNotInitializedException("Your person may not contain" +
                    " malformed fields.");
        }

        final Optional<City> cityBirthDBOpt =
                cityRepository.findByPtpbr(p.birthCityCode());

        if(cityBirthDBOpt.isEmpty()){
            throw new UnknownCityException("Not a valid city of birth.");
        }

        final Optional<City> cityOfResidenceDBOpt =
                cityRepository.findByPtpbr(p.residenceCityCode());

        if(cityOfResidenceDBOpt.isEmpty()){
            throw new UnknownCityException("Not a valid city of residence.");
        }

        final City cityOfBirth = cityBirthDBOpt.get();
        final City cityOfResidence = cityOfResidenceDBOpt.get();

        final Person personToSave = personSaveConverter.toEntity(p);
        personToSave.setCityOfBirth(cityOfBirth);
        personToSave.setCityOfResidence(cityOfResidence);

        return personSaveConverter
                .toDto(personRepository.save(personToSave));
    }

    @Override
    public void deletePerson(Long id) throws Exception {
        if(id == null){
            throw new NullPointerException("Your id may not be null.");
        }
        personRepository.deleteById(id);
    }

    @Override
    public Person updatePerson(PersonSaveDTO p) throws Exception {
        return null;
    }
}

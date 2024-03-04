package rs.ac.bg.fon.tps_backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonDisplayConverter;
import rs.ac.bg.fon.tps_backend.converter.impl.PersonSaveConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.UnknownCityException;
import rs.ac.bg.fon.tps_backend.repository.CityRepository;
import rs.ac.bg.fon.tps_backend.repository.PersonRepository;
import rs.ac.bg.fon.tps_backend.service.PersonService;
import rs.ac.bg.fon.tps_backend.validator.PersonValidator;

import java.util.List;
import java.util.Optional;

@Service("personServiceImpl")
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final CityRepository cityRepository;
    private final PersonSaveConverter personSaveConverter;
    private final PersonDisplayConverter personDisplayConverter;
    private final PersonValidator personValidator;
    @Override
    public List<PersonDisplayDTO> getAll() {
        return personDisplayConverter.listToDTO(
            personRepository.findAll()
        );
    }

    @Override
    public PersonSaveDTO savePerson(PersonSaveDTO p) throws Exception {
        personValidator.validateForSave(p);
        personValidator.setLastNameToJovanovicDefault(p);

        final City cityOfBirth = fetchCityIfPossible(p.birthCityCode());
        final City cityOfResidence = fetchCityIfPossible(p.residenceCityCode());
        final Person personToSave = personSaveConverter.toEntity(p);

        personToSave.setCityOfBirth(cityOfBirth);
        personToSave.setCityOfResidence(cityOfResidence);

        return personSaveConverter
                .toDto(personRepository.save(personToSave));
    }

    private City fetchCityIfPossible(int cityCode){
        final Optional<City> cityDBOpt =
                cityRepository.findByPptbr(cityCode);

        if(cityDBOpt.isEmpty()){
            throw new UnknownCityException("Person contains reference to unknown city.");
        }

        return cityDBOpt.get();
    }

    private Person fetchPersonIfPossible(Long id){
        final var personDbOpt =
                personRepository.findById(id);

        if(personDbOpt.isEmpty()){
            throw new EntityNotFoundException("The person with given ID may not exist.");
        }

        return personDbOpt.get();
    }

    @Override
    public void deletePerson(Long id) throws Exception {
        if(id == null){
            throw new NullPointerException("Your id may not be null.");
        }
        if(!personRepository.existsById(id)){
            throw new EntityNotFoundException("Person with said id does not exist.");
        }
        personRepository.deleteById(id);
    }

    @Override
    public PersonSaveDTO updatePerson(PersonSaveDTO p) throws Exception {
        personValidator.validateForSave(p);
        personValidator.validateUpdateId(p);
        personValidator.setLastNameToJovanovicDefault(p);

        final Person personFromDb = fetchPersonIfPossible(p.id());
        final City cityOfBirth = fetchCityIfPossible(p.birthCityCode());
        final City cityOfResidence = fetchCityIfPossible(p.residenceCityCode());

        final Person personToSave =
                personSaveConverter.toEntity(p);

        personToSave.setCityOfBirth(cityOfBirth);
        personToSave.setCityOfResidence(cityOfResidence);

        return personSaveConverter
                .toDto(personRepository.save(personToSave));
    }
}

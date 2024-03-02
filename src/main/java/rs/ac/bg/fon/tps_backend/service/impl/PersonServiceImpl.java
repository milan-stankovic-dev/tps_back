package rs.ac.bg.fon.tps_backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.tps_backend.constraints.PersonConstraints;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
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

        final Optional<City> cityBirthDBOpt =
                cityRepository.findByPptbr(p.birthCityCode());

        if(cityBirthDBOpt.isEmpty()){
            throw new UnknownCityException("Not a valid city of birth.");
        }

        final Optional<City> cityOfResidenceDBOpt =
                cityRepository.findByPptbr(p.residenceCityCode());

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
    public PersonSaveDTO updatePerson(PersonSaveDTO p) throws Exception {
        personValidator.validateForSave(p);
        personValidator.validateUpdateId(p);

        final var personDbOpt =
                personRepository.findById(p.id());

        if(personDbOpt.isEmpty()){
            throw new EntityNotFoundException("The person with given ID may not exist.");
        }
        final Person personFromDb = personDbOpt.get();

        final var cityBirthDbOpt =
                cityRepository.findByPptbr(p.birthCityCode());

        if(cityBirthDbOpt.isEmpty()){
            throw new EntityNotFoundException("The city of birth does not exist.");
        }
        final City cityOfBirth = cityBirthDbOpt.get();

        final var cityResidenceDbOpt =
                cityRepository.findByPptbr(p.residenceCityCode());

        if(cityResidenceDbOpt.isEmpty()){
            throw new EntityNotFoundException("The city of residence does not exist.");
        }
        final City cityOfResidence = cityResidenceDbOpt.get();
        final Person personToSave =
                personSaveConverter.toEntity(p);

        personToSave.setCityOfBirth(cityOfBirth);
        personToSave.setCityOfResidence(cityOfResidence);

        return personSaveConverter
                .toDto(personRepository.save(personToSave));
    }
}

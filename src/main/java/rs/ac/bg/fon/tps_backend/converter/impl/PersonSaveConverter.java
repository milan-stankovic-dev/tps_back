package rs.ac.bg.fon.tps_backend.converter.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.converter.DTOEntityConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;

@Component
public class PersonSaveConverter implements DTOEntityConverter<PersonSaveDTO, Person> {
    @Override
    public Person toEntity(PersonSaveDTO personSaveDTO) {
        final var cityOfBirth = City.builder()
                .pptbr(personSaveDTO.birthCityCode())
                .build();
        final var cityOfResidence = City.builder()
                .pptbr(personSaveDTO.residenceCityCode())
                .build();

        return new Person(personSaveDTO.id(),
                personSaveDTO.firstName(),
                personSaveDTO.lastName(),
                personSaveDTO.heightInCm(),
                personSaveDTO.dOB(),
                0,
                cityOfBirth, cityOfResidence);
    }

    @Override
    public PersonSaveDTO toDto(Person person) {
        return new PersonSaveDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getHeightInCm(),
                person.getDOB(),
                person.getCityOfBirth() == null? null :
                        person.getCityOfBirth().getPptbr(),
                person.getCityOfResidence() == null? null :
                        person.getCityOfResidence().getPptbr()
        );
    }
}

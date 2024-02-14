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
                .ptpbr(personSaveDTO.birthCityCode())
                .build();
        final var cityOfResidence = City.builder()
                .ptpbr(personSaveDTO.residenceCityCode())
                .build();

        return new Person(personSaveDTO.id(),
                personSaveDTO.firstName(),
                personSaveDTO.lastName(),
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
                person.getDOB(),
                person.getCityOfBirth() == null? null :
                        person.getCityOfBirth().getPtpbr(),
                person.getCityOfResidence() == null? null :
                        person.getCityOfResidence().getPtpbr()
        );
    }
}

package rs.ac.bg.fon.tps_backend.converter.impl;

import lombok.val;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.converter.DTOEntityConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;

@Component
public class PersonSaveConverter implements DTOEntityConverter<PersonSaveDTO, Person> {
    @Override
    public Person toEntity(PersonSaveDTO personSaveDTO) {
        if(personSaveDTO == null){
            return null;
        }

        val cityOfBirth = City.builder()
                .pptbr(personSaveDTO.birthCityCode())
                .build();
        val cityOfResidence = City.builder()
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
        return person == null ? null : new PersonSaveDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getHeightInCm(),
                person.getDOB(),
                person.getCityOfBirth() == null? 0 :
                        person.getCityOfBirth().getPptbr(),
                person.getCityOfResidence() == null? 0 :
                        person.getCityOfResidence().getPptbr()
        );
    }
}

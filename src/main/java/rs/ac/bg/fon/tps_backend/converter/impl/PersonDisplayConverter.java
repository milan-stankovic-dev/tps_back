package rs.ac.bg.fon.tps_backend.converter.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.converter.DTOEntityConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;

@Component
public class PersonDisplayConverter implements DTOEntityConverter<PersonDisplayDTO, Person> {

    @Override
    public Person toEntity(PersonDisplayDTO personDisplayDTO) {
        return personDisplayDTO == null ? null : new Person(personDisplayDTO.id(),
                personDisplayDTO.firstName(),
                personDisplayDTO.lastName(),
                personDisplayDTO.heightInCm(),
                personDisplayDTO.dOB(),
                personDisplayDTO.ageInMonths(),
                City.builder()
                        .name(personDisplayDTO.cityOfBirthName())
                        .pptbr(personDisplayDTO.cityOfBirthPPTBR())
                        .build(),
                City.builder()
                        .name(personDisplayDTO.cityOfResidenceName())
                        .pptbr(personDisplayDTO.cityOfResidencePPTBR())
                        .build()
        );
    }

    @Override
    public PersonDisplayDTO toDto(Person person) {
        return person == null ? null : new PersonDisplayDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getHeightInCm(),
                person.getDOB(),
                person.getAgeInMonths(),
                person.getCityOfBirth() == null ?
                        null: person.getCityOfBirth().getName(),
                person.getCityOfBirth() == null ?
                        0: person.getCityOfBirth().getPptbr(),
                person.getCityOfResidence() == null ?
                        null: person.getCityOfResidence().getName(),
                person.getCityOfResidence() == null ?
                        0: person.getCityOfResidence().getPptbr()
        );
    }
}

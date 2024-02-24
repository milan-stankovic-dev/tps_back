package rs.ac.bg.fon.tps_backend.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.converter.DTOEntityConverter;
import rs.ac.bg.fon.tps_backend.domain.City;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;

@Component
@RequiredArgsConstructor
public class PersonDisplayConverter implements DTOEntityConverter<PersonDisplayDTO, Person> {
    private final CityConverter cityConverter;

    @Override
    public Person toEntity(PersonDisplayDTO personDisplayDTO) {
        return new Person(personDisplayDTO.id(),
                personDisplayDTO.firstName(),
                personDisplayDTO.lastName(),
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
        return new PersonDisplayDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getDOB(),
                person.getAgeInMonths(),
                person.getCityOfBirth() == null ?
                        null: person.getCityOfBirth().getName(),
                person.getCityOfBirth() == null ?
                        null: person.getCityOfBirth().getPptbr(),
                person.getCityOfResidence() == null ?
                        null: person.getCityOfResidence().getName(),
                person.getCityOfResidence() == null ?
                        null: person.getCityOfResidence().getPptbr()
        );
    }
}

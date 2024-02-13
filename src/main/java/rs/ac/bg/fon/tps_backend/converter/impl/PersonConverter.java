package rs.ac.bg.fon.tps_backend.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.converter.DTOEntityConverter;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDTO;

@Component
@RequiredArgsConstructor
public class PersonConverter implements DTOEntityConverter<PersonDTO, Person> {
    private final CityConverter cityConverter;

    @Override
    public Person toEntity(PersonDTO personDTO) {
        return new Person(personDTO.id(),
                personDTO.firstName(),
                personDTO.lastName(),
                personDTO.dOB(),
                personDTO.ageInMonths(),
                cityConverter.toEntity(
                        personDTO.cityOfBirth()
                ),
                cityConverter.toEntity(
                        personDTO.cityOfResidence()
                ));
    }

    @Override
    public PersonDTO toDto(Person person) {
        return new PersonDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getDOB(),
                person.getAgeInMonths(),
                cityConverter.toDto(
                        person.getCityOfBirth()
                ),
                cityConverter.toDto(
                        person.getCityOfResidence()
                )
        );
    }
}

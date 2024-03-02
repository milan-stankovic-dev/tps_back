package rs.ac.bg.fon.tps_backend.validator;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.constraints.PersonConstraints;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;
import rs.ac.bg.fon.tps_backend.util.StringConverterUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PersonValidator {
    private void validateForNull(PersonSaveDTO p) {
        if(p == null){
            throw new PersonNotInitializedException("Your person may not be null.");
        }

        if(p.firstName() == null || p.lastName() == null ||
                p.dOB() == null) {
            throw new PersonNotInitializedException("Your person may not contain" +
                    " malformed fields.");
        }
    }
    public void validateUpdateId(PersonSaveDTO p) {
        if(p.id() == null) {
            throw new PersonNotInitializedException("Your person's ID may not be null.");
        }
    }

    private void validateNameFormat(String nameOrLastName) {
        if(Character.isLowerCase(nameOrLastName.charAt(0))) {
            throw new PersonNotInitializedException("Person's name or last name may not start with" +
                    " a lowercase letter.");
        }

        val characterListOfString = StringConverterUtil
                .stringToCharList(nameOrLastName);

        characterListOfString
                .forEach(ch -> {

                    if(!Character.isAlphabetic(ch)){
                        throw new PersonNotInitializedException("Person's name contains illegal characters.");
                    }

                    if(characterListOfString.indexOf(ch) != 0 &&
                            Character.isUpperCase(ch)){
                        throw new PersonNotInitializedException("Person's name contains uppercase letters.");
                    }
                });
    }

    private void validateConstraints(PersonSaveDTO person, @NonNull PersonConstraints constraints) {
        if(constraints == null) {
            throw new IllegalStateException("Constraints must be given for checking the person object.");
        }

        if(person.firstName().length() > constraints.firstNameMaxLen() ||
            person.firstName().length() < constraints.firstNameMinLen()) {
            throw new PersonNotInitializedException("Person's name length is invalid.");
        }

        if(person.lastName().length() > constraints.lastNameMaxLen() ||
                person.lastName().length() < constraints.lastNameMinLen()) {
            throw new PersonNotInitializedException("Person's last name length is invalid.");
        }

        if(person.heightInCm() > constraints.heightInCmMax() ||
            person.heightInCm() < constraints.heightInCmMin()) {
            throw new PersonNotInitializedException("Person's height is invalid.");
        }

        if(person.dOB().isAfter(constraints.latestDOB()) ||
            person.dOB().isBefore(constraints.earliestDOB())) {
            throw new PersonNotInitializedException("Person's date of birth is invalid.");
        }
    }

    public void validateForSave(PersonSaveDTO person) {
        validateForSave(person, new PersonConstraints(
                2,30,2,30,
                70, 260, LocalDate.of(1950, 1,1),
                LocalDate.of(2006,1,1)));
    }
    public void validateForSave(PersonSaveDTO person, PersonConstraints constraints) {
        validateForNull(person);
        validateConstraints(person, constraints);
        validateNameFormat(person.firstName());
        validateNameFormat(person.lastName());

    }
}

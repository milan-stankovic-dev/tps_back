package rs.ac.bg.fon.tps_backend.validator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.constants.PersonConstraintsConstants;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;
import rs.ac.bg.fon.tps_backend.util.StringConverterUtil;

@Component
@RequiredArgsConstructor
public class PersonValidator {
    private final StringConverterUtil stringConverter;
    private final PersonConstraintsConstants constants;

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

        val characterListOfString = stringConverter
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

    private void validateConstraints(PersonSaveDTO person, @NonNull PersonConstraintsConstants constraints) {
        if(constraints == null) {
            throw new IllegalStateException("Constraints must be given for checking the person object.");
        }

        if(person.firstName().length() > constraints.getFIRST_NAME_MAX_LENGTH() ||
            person.firstName().length() < constraints.getFIRST_NAME_MIN_LENGTH()) {
            throw new PersonNotInitializedException("Person's name length is invalid.");
        }

        if(person.lastName().length() > constraints.getLAST_NAME_MAX_LENGTH() ||
                person.lastName().length() < constraints.getLAST_NAME_MIN_LENGTH()) {
            throw new PersonNotInitializedException("Person's last name length is invalid.");
        }

        if(person.heightInCm() > constraints.getHEIGHT_IN_CM_MAX() ||
            person.heightInCm() < constraints.getHEIGHT_IN_CM_MIN()) {
            throw new PersonNotInitializedException("Person's height is invalid.");
        }

        if(person.dOB().getYear() > constraints.getDOB_LATEST_YEAR() ||
            person.dOB().getYear() < constraints.getDOB_EARLIEST_YEAR()) {
            throw new PersonNotInitializedException("Person's date of birth is invalid.");
        }
    }

    public void validateForSave(PersonSaveDTO person) {
        validateForSave(person, constants);
    }

    public void validateForSave(PersonSaveDTO person, PersonConstraintsConstants constraints) {
        validateForNull(person);
        validateConstraints(person, constraints);
        validateNameFormat(person.firstName());
        validateNameFormat(person.lastName());

    }
}

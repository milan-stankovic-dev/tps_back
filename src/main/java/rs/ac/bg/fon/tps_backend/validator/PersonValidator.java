package rs.ac.bg.fon.tps_backend.validator;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;

@UtilityClass
public class PersonValidator {
    public void validatePersonSaveDTO(PersonSaveDTO p) {
        if(p == null){
            throw new PersonNotInitializedException("Your person may not be null.");
        }

        if(p.firstName() == null || p.lastName() == null ||
                p.dOB() == null){
            throw new PersonNotInitializedException("Your person may not contain" +
                    " malformed fields.");
        }
    }
}

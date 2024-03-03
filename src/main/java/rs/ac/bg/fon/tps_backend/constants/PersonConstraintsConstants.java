package rs.ac.bg.fon.tps_backend.constants;

import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.util.PropertyUtil;

import java.io.IOException;

@Component
@Getter
@ToString
public final class PersonConstraintsConstants {
    private final PropertyUtil propertyUtil;
    private final String PATH = "constants/person_constraints.properties";
    private final int FIRST_NAME_MIN_LENGTH;
    private final int FIRST_NAME_MAX_LENGTH;
    private final int LAST_NAME_MIN_LENGTH;
    private final int LAST_NAME_MAX_LENGTH;
    private final int HEIGHT_IN_CM_MIN;
    private final int HEIGHT_IN_CM_MAX;
    private final int DOB_EARLIEST_YEAR;
    private final int DOB_LATEST_YEAR;


    public PersonConstraintsConstants(PropertyUtil propertyUtil) throws IOException {
        this.propertyUtil = propertyUtil;
        FIRST_NAME_MIN_LENGTH = Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "first_name_min_length"));
        FIRST_NAME_MAX_LENGTH = Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "first_name_max_length"));
        LAST_NAME_MIN_LENGTH = Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "last_name_min_length"));
        LAST_NAME_MAX_LENGTH = Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "last_name_max_length"));
        HEIGHT_IN_CM_MIN = Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "height_in_cm_min"));
        HEIGHT_IN_CM_MAX = Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "height_in_cm_max"));
        DOB_EARLIEST_YEAR= Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "dob_earliest_year"));
        DOB_LATEST_YEAR = Integer.parseInt(propertyUtil.getPropertyFromFile(
                PATH, "dob_latest_year"));
    }


}

package rs.ac.bg.fon.tps_backend.constants;

import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.util.PropertyUtil;

import java.io.IOException;

@Component
@Getter
@ToString
public final class DateConstant {
    private final PropertyUtil propertyUtil;
    private final String PATH = "constants/date_format.properties";
    private final String DATE_FORMAT;


    public DateConstant(PropertyUtil propertyUtil) throws IOException {
        this.propertyUtil = propertyUtil;
        DATE_FORMAT = propertyUtil.getPropertyFromFile(
                PATH, "date_format");
    }
}

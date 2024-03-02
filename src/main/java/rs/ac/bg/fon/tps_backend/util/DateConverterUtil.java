package rs.ac.bg.fon.tps_backend.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Component
public class DateConverterUtil {
    public LocalDate dateToLocalDate(Date date) {
        val year = date.getYear() + 1900;
        val month = date.getMonth() + 1;
        val day = date.getDay() + 1;

        return LocalDate.of(year, month, day);
    }

    public LocalDate localDateToDate(LocalDate localDate) {
        val year = localDate.getYear() - 1900;
        val month = localDate.getMonth().getValue() - 1;
        val day = localDate.getDayOfMonth() - 1;

        return LocalDate.of(year, month, day);
    }

}

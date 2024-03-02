package rs.ac.bg.fon.tps_backend.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@UtilityClass
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

    @SneakyThrows(ParseException.class)
    public LocalDate stringToLocalDate(String inputDate) {
        final DateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        final Date helperDateVar = simpleDateFormat.parse(inputDate);

        return dateToLocalDate(helperDateVar);
    }
}

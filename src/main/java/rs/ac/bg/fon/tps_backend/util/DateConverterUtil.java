package rs.ac.bg.fon.tps_backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class DateConverterUtil {
    public LocalDate utilDateToLocalDate(java.util.Date date) {
        if(date == null){
            throw new IllegalArgumentException("Date may not be null.");
        }

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public LocalDate sqlDateToLocalDate(java.sql.Date date){
        if(date == null) {
            throw new IllegalArgumentException("Date may not be null.");
        }

        return date.toLocalDate();
    }

    public java.util.Date localDateToDate(LocalDate localDate) {
        if(localDate == null){
            throw new IllegalArgumentException("Date may not be null.");
        }
        return Date.from(localDate.atStartOfDay(
                ZoneId.systemDefault()).toInstant());
    }

}

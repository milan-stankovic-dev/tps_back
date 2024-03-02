package rs.ac.bg.fon.tps_backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StringConverterUtil {
    public List<Character> stringToCharList(String input) {
        return input.chars()
                .mapToObj(c -> (char) c)
                .toList();
    }
}

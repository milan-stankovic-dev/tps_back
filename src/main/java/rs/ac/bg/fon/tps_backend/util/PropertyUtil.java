package rs.ac.bg.fon.tps_backend.util;

import lombok.Cleanup;
import lombok.val;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class PropertyUtil {
    public  Map<String, String> propertiesFromFile(String filePath) throws IOException {
        val properties = new Properties();
        final Map<String, String> result = new HashMap<>();

        @Cleanup
        final InputStream input = getClass().getClassLoader().getResourceAsStream(filePath);

        if(input == null){
            throw new IOException("File not found");
        }

        properties.load(input);

        properties.stringPropertyNames()
                .forEach(key -> {
                    result.put(key, properties.getProperty(key));
                });

        return result;
    }

    public String getPropertyFromFile(String filePath, String property) throws IOException {
        final Map<String,String> result = propertiesFromFile(filePath);
        val retVal = result.get(property);

        if(retVal == null){
            throw new IllegalArgumentException("Unknown property");
        }

        return retVal;
    }
}

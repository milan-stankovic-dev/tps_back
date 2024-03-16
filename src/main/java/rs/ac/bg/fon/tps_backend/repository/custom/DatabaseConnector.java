package rs.ac.bg.fon.tps_backend.repository.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.tps_backend.util.PropertyUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DatabaseConnector {
    private final PropertyUtil propertyUtil;

    public Connection getConnection(String appPropertiesUrl)
            throws SQLException, IOException {

        final String url = propertyUtil.getPropertyFromFile(appPropertiesUrl,
                "spring.datasource.url");
        final String username = propertyUtil.getPropertyFromFile(appPropertiesUrl,
                "spring.datasource.username");
        final String password = propertyUtil.getPropertyFromFile(appPropertiesUrl,
                "spring.datasource.password");

        return DriverManager.getConnection(url, username, password);
    }
}
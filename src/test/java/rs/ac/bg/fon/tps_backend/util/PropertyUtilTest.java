package rs.ac.bg.fon.tps_backend.util;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;

@SpringBootTest
class PropertyUtilTest {
    private final PropertyUtil propertyUtil;

    @Autowired
    public PropertyUtilTest(PropertyUtil propertyUtil) {
        this.propertyUtil = propertyUtil;
    }

    @Test
    @DisplayName("Reading properties from unknown location")
    void readPropertiesFromWrongLocationTest() throws IOException {
        assertThatThrownBy(() -> propertyUtil.propertiesFromFile("wrongLocation"))
                .isInstanceOf(IOException.class)
                .hasMessage("File not found");
    }

    @Test
    @DisplayName("Reading specific property from unknown location")
    void readOnePropertyFromWrongLocationTest() throws IOException {
        assertThatThrownBy(() -> propertyUtil.getPropertyFromFile("wrongLocation", "foo_prop"))
                .isInstanceOf(IOException.class)
                .hasMessage("File not found");
    }

    @Test
    @DisplayName("Reading properties correctly")
    void readPropertiesCorrectly() throws IOException {
        val properties = propertyUtil.propertiesFromFile(
                "properties/test_properties.properties");
        final Map<String,String> expectedMap = new HashMap<>();
        expectedMap.put("foo_prop", "foo");
        expectedMap.put("bar_prop", "bar");
        expectedMap.put("baz_prop", "baz");

        assertThat(expectedMap)
                .isEqualTo(properties);
    }

    @ParameterizedTest
    @CsvSource({
            "foo_prop, foo",
            "bar_prop, bar",
            "baz_prop, baz"
    })
    @DisplayName("Reading certain property correctly")
    void readOnePropertyCorrectly(String prop, String value) throws IOException {
        val propertyRead = propertyUtil.getPropertyFromFile(
                "properties/test_properties.properties", prop);

        assertThat(value)
                .isEqualTo(propertyRead);
    }

    @Test
    @DisplayName("Reading unknown property from correct path")
    void readOnePropertyIncorrectly() throws IOException {
       assertThatThrownBy(()->propertyUtil.getPropertyFromFile(
               "properties/test_properties.properties", "foobar_prop"))
               .isInstanceOf(IllegalArgumentException.class)
               .hasMessage("Unknown property");
    }



}



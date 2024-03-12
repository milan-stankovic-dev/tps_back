package rs.ac.bg.fon.tps_backend.util;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StringConverterUtilTest {
    private final StringConverterUtil converter;

    @Autowired
    public StringConverterUtilTest(StringConverterUtil converter) {
        this.converter = converter;
    }

    @Test
    @DisplayName("Null string converted to list")
    void nullStringConvertToList() {
        assertThrows(IllegalArgumentException.class, () -> converter.stringToCharList(null),
                "Input must not be null.");
    }

    @Test
    @DisplayName("Empty string converted to list of chars")
    void emptyStringConvertToList() {
        val resultOfConversion = converter.stringToCharList("");
        assertTrue(resultOfConversion.isEmpty());
    }

    @Test
    @DisplayName("Single blank string converted to list of chars")
    void singleBlankStringConvertToList() {
        val resultOfConversion = converter.stringToCharList(" ");
        assertEquals(1, resultOfConversion.size());
        assertTrue(resultOfConversion.contains(' '));
    }

    @Test
    @DisplayName("Hello, World! string converted to list")
    void helloWorldStringToListOfCharsTest() {
        val resultOfConversion = converter.stringToCharList("Hello, World!");
        assertThat(resultOfConversion)
                .isInstanceOf(List.class)
                .contains('H','e','l','o',' ',',','W','o','r','d','!');
        assertThat(resultOfConversion.size())
                .isEqualTo(13);
    }
}


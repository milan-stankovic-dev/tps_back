package rs.ac.bg.fon.tps_backend.util;

import lombok.val;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class StringConverterUtilTest {

    private static StringConverterUtil converter;

    @BeforeClass
    public static void beforeClass() throws Exception {
        converter = new StringConverterUtil();
    }

    @AfterClass
    public static void afterClass() {
        converter = null;
    }

    @Test
    @DisplayName("Null string converted to list")
    public void nullStringConvertToList() {
        assertThatThrownBy(()-> converter.stringToCharList(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must not be null.");
    }

    @Test
    @DisplayName("Empty string converted to list of chars")
    public void emptyStringConvertToList() {
        val resultOfConversion = converter.stringToCharList("");
        assertThat(resultOfConversion)
                .isInstanceOf(List.class)
                .isEmpty();
    }

    @Test
    @DisplayName("Single blank string converted to list of chars")
    public void singleBlankStringConvertToList() {
        val resultOfConversion = converter.stringToCharList(" ");
        assertThat(resultOfConversion)
                .isInstanceOf(List.class)
                .contains(' ');
        assertThat(resultOfConversion.size())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Hello, World! string converted to list")
    public void helloWorldStringToListOfCharsTest() {
        val resultOfConversion = converter.stringToCharList("Hello, World!");
        assertThat(resultOfConversion)
                .isInstanceOf(List.class)
                .contains('H','e','l','o',' ',',','W','o','r','d','!');
        assertThat(resultOfConversion.size())
                .isEqualTo(13);
    }
}

package rs.ac.bg.fon.tps_backend.converter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class DTOEntityConverterTest {
    private final DTOEntityConverter<EntityDTOExample, EntityExample> converter = new DTOEntityConverter<>() {
        @Override
        public EntityExample toEntity(EntityDTOExample d) {
            return d == null ? null : new EntityExample(
                    d.foo(), d.bar(), d.baz()
            );
        }

        @Override
        public EntityDTOExample toDto(EntityExample e) {
            return e == null ? null : new EntityDTOExample(
                e.getFoo(), e.getBar(), e.getBaz()
            );
        }
    };

    @Test
    @DisplayName("List to DTO test null list")
    void listToDtoNullListTest() {
        assertThatThrownBy(()-> converter.listToDTO(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("List of entities may not be null");
    }

    @Test
    @DisplayName("List to DTO test empty list")
    void listToDtoEmptyListTest() {
        val inputList =
                new ArrayList<EntityExample>();
        val expectedResult =
                new ArrayList<EntityDTOExample>();
        val actualResult = converter.listToDTO(inputList);

        assertThat(actualResult).isInstanceOf(List.class);
        assertThat(actualResult)
                .isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("List to DTO test normal list")
    void listToDtoNormalListTest() {
        val inputList = List.of(
                new EntityExample("Text1", 1,
                        LocalDate.of(2000,1,1)),
                new EntityExample("Text2", 2,
                        LocalDate.of(2020,10,10)),
                new EntityExample("Text3", 3,
                        LocalDate.of(2003,3,3))
        );
        val expectedResult = List.of(
                new EntityDTOExample("Text1", 1,
                        LocalDate.of(2000, 1,1)),
                new EntityDTOExample("Text2", 2,
                        LocalDate.of(2020, 10,10)),
                new EntityDTOExample("Text3", 3,
                        LocalDate.of(2003, 3,3))
        );
        val actualResult = converter.listToDTO(inputList);

        assertThat(actualResult).isInstanceOf(List.class);
        assertThat(actualResult)
                .isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("List to entity test null list")
    void listToEntityNullListTest() {
        assertThatThrownBy(()-> converter.listTOEntity(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("List of DTOs may not be null");
    }

    @Test
    @DisplayName("List to entities test empty list")
    void listToEntitiesEmptyListTest() {
        val inputList =
                new ArrayList<EntityDTOExample>();
        val expectedResult =
                new ArrayList<EntityExample>();
        val actualResult = converter.listTOEntity(inputList);

        assertThat(actualResult).isInstanceOf(List.class);
        assertThat(actualResult)
                .isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("List to entity test normal list")
    void listToEntityNormalListTest() {
        val inputList = List.of(
                new EntityDTOExample("Text1", 1,
                        LocalDate.of(2000,1,1)),
                new EntityDTOExample("Text2", 2,
                        LocalDate.of(2020,10,10)),
                new EntityDTOExample("Text3", 3,
                        LocalDate.of(2003,3,3))
        );
        val expectedResult = List.of(
                new EntityExample("Text1", 1,
                        LocalDate.of(2000, 1,1)),
                new EntityExample("Text2", 2,
                        LocalDate.of(2020, 10,10)),
                new EntityExample("Text3", 3,
                        LocalDate.of(2003, 3,3))
        );
        val actualResult = converter.listTOEntity(inputList);

        assertThat(actualResult).isInstanceOf(List.class);
        assertThat(actualResult)
                .isEqualTo(expectedResult);
    }

    @Data
    @AllArgsConstructor
    private static class EntityExample {
        private String foo;
        private int bar;
        private LocalDate baz;
    }

    private record EntityDTOExample(
        String foo,
        int bar,
        LocalDate baz
    ){ }
}

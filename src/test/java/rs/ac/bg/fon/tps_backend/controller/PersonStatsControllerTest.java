package rs.ac.bg.fon.tps_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.bg.fon.tps_backend.service.PersonService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PersonStatsController.class)
class PersonStatsControllerTest {
    private final String apiUrl = "/person/stats";
    @Autowired
    private MockMvc mvc;
    @MockBean
    @Qualifier("personTemplateServiceImpl")
    private PersonService personService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Get max height test")
    void getMaxHeightTest() throws Exception {
        val expectedResult = 190;
        when(personService.getMaxHeight())
                .thenReturn(190);

        final String JSONResponse = mvc.perform(
                get(apiUrl + "/maxHeight")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();
        val actualResult =
                objectMapper.readValue(JSONResponse, Integer.class);
        assertThat(expectedResult)
                .isEqualTo(actualResult);
        verify(personService,times(1))
                .getMaxHeight();

    }

    @Test
    @DisplayName("Get average age in years test")
    void getAverageAgeInYearsTest() throws Exception{
        val expectedResult = 30.42;
        when(personService.getAverageAgeYears())
                .thenReturn(expectedResult);

        final String JSONResponse =
                mvc.perform(get(apiUrl + "/averageAgeYears")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        val actualResult =
                objectMapper.readValue(JSONResponse, Double.class);

        assertThat(expectedResult)
                .isEqualTo(actualResult);
        verify(personService,times(1))
                .getAverageAgeYears();
    }
}

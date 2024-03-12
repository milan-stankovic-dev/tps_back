package rs.ac.bg.fon.tps_backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.service.PersonService;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = PersonViewsController.class)
class PersonViewsControllerTest {
    final String requestUrl = "/person/views";
    @Autowired
    private MockMvc mvc;
    @MockBean
    @Qualifier("personTemplateServiceImpl")
    private PersonService personService;
    @Autowired
    private ObjectMapper objectMapper;
    private List<PersonDisplayDTO> personDTOs;

    @BeforeEach
    void setUp() {
        val person1 = new PersonDisplayDTO(
                1L, "Pera", "Peric",
                189, LocalDate.of(2000,1,1),0,
                "Zajecar",11_300, "Beograd",
                11_300
        );
        val person2 = new PersonDisplayDTO(
                2L, "Sava", "Savic",
                179, LocalDate.of(1990,2,2),0,
                "Beograd", 11_300, "Beograd",
                11_300
        );
        personDTOs = List.of(person1, person2);
    }

    @Test
    @DisplayName("Get all Smederevci test")
    void getAllSmederevoTest() throws Exception {
        when(personService.getAllSmederevci())
                .thenReturn(personDTOs);

        final String JSONResponse = mvc.perform(get(requestUrl + "/smederevo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();
        final List<PersonDisplayDTO> persons =
                objectMapper.readValue(JSONResponse,
                        new TypeReference<List<PersonDisplayDTO>>() {});

        assertThat(persons.get(0))
                .isEqualTo(personDTOs.get(0));
        assertThat(persons.get(1))
                .isEqualTo(personDTOs.get(1));
        verify(personService,times(1))
                .getAllSmederevci();
    }

    @Test
    @DisplayName("Get all adults test")
    void getAllAdultsTest() throws Exception {
        when(personService.getAllAdults())
                .thenReturn(personDTOs);

        final String JSONResponse = mvc.perform(get(requestUrl + "/adults")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();
        final List<PersonDisplayDTO> persons =
                objectMapper.readValue(JSONResponse,
                        new TypeReference<List<PersonDisplayDTO>>() {});

        assertThat(persons.get(0))
                .isEqualTo(personDTOs.get(0));
        assertThat(persons.get(1))
                .isEqualTo(personDTOs.get(1));
        verify(personService, times(1))
                .getAllAdults();
    }

}

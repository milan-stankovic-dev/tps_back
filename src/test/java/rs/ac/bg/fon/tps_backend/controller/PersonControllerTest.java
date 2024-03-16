package rs.ac.bg.fon.tps_backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.exception.PersonNotInitializedException;
import rs.ac.bg.fon.tps_backend.service.PersonService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = PersonController.class)
class PersonControllerTest {
    final String requestUrl = "/person";
    @Autowired
    private MockMvc mvc;

    @MockBean
    @Qualifier("personTemplateServiceImpl")
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<PersonDisplayDTO> personsDisplay;

    private PersonSaveDTO personSave;
    @BeforeEach
    void setUp() {
        val person1 = new PersonDisplayDTO(
            1L, "Pera", "Peric",
                189, LocalDate.of(2000,1,1),0,
                "Zajecar",19_000, "Beograd",
                11_000
        );
        val person2 = new PersonDisplayDTO(
                2L, "Sava", "Savic",
                179, LocalDate.of(1990,2,2),0,
                "Beograd", 11_000, "Beograd",
                11_000
        );

        personSave = new PersonSaveDTO(
                3L, "Sara","Saric", 165,
                LocalDate.of(2001,10,10),19_000,
                19_000
        );

        personsDisplay = List.of(person1, person2);
    }

    @Test
    @DisplayName("Get all persons test")
    void getAllTest() throws Exception {
        when(personService.getAll()).thenReturn(personsDisplay);

        val JSONResponse = mvc.perform(get("/person")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();

        val responseList = objectMapper.readValue(JSONResponse,
                new TypeReference<List<PersonDisplayDTO>>() {});

        assertThat(responseList)
                .containsExactlyInAnyOrderElementsOf(personsDisplay);
    }

    @Test
    @DisplayName("Save person test")
    void savePersonTest() throws Exception {
        val personToSave = new PersonSaveDTO(
                  10L, "Jovana", "Jovic",
                160, LocalDate.of(1998,10,10),
                11_000,11_000
        );
        val JSONRequest = objectMapper.writeValueAsString(personToSave);

        when(personService.savePerson(personToSave))
                .thenReturn(personToSave);

        val JSONResponse = mvc.perform(post(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONRequest))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();

        val responsePersonSaveDTO = objectMapper.readValue(JSONResponse,
            PersonSaveDTO.class);

        assertThat(personToSave)
                .isEqualTo(responsePersonSaveDTO);
    }

    @Test
    @DisplayName("Save person bad payload test")
    void savePersonBadPayloadTest() throws Exception {
        final String badJSONRequest = "This is not real JSON!";

        val JSONResponse = mvc.perform(post(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJSONRequest))
                .andExpect(status().isBadRequest()).andReturn().getResponse()
                .getContentAsString();

        final Map<String, String> responseMap =
                objectMapper.readValue(JSONResponse,
                        new TypeReference<>() {});
        assertThat(responseMap)
                .containsEntry("detail", "Failed to read request")
                .containsEntry("status", "400")
                .containsEntry("title", "Bad Request");
        verify(personService, never())
                .savePerson(any(PersonSaveDTO.class));
    }

    @Test
    @DisplayName("Save malformed person error test")
    void savePersonMalformedPersonTest() throws Exception {
        val personToSave = new PersonSaveDTO(
                null, null, "Jovic",
                170,null, 11_000, 11_000
        );

        val JSONRequest = objectMapper.writeValueAsString(personToSave);

        when(personService.savePerson(personToSave))
                .thenThrow(new PersonNotInitializedException(
                        "Your person may not contain malformed fields."
                ));

        val JSONResponse = mvc.perform(post(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONRequest))
                .andExpect(status().isNotFound()).andReturn().getResponse()
                .getContentAsString();

        final Map<String, String> responseMap =
                objectMapper.readValue(JSONResponse,
                        new TypeReference<>() {});
        assertThat(responseMap)
                .containsEntry("message",
                        "Your person may not contain malformed fields.");
    }

    @Test
    @DisplayName("Delete person test")
    void deletePersonTest() throws Exception {

        doNothing().when(personService)
                .deletePerson(1L);

        val JSONResponse = mvc.perform(delete(requestUrl + "/{id}",1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Delete non existent person test")
    void deleteNonExistentPersonTest() throws Exception {

        doThrow(new EntityNotFoundException("Person with said id does not exist"))
                .when(personService).deletePerson(100L);

        val JSONResponse = mvc.perform(delete(requestUrl + "/{id}",100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn().getResponse()
                .getContentAsString();

        final Map<String, String> responseMap =
                objectMapper.readValue(JSONResponse,
                        new TypeReference<>() {});

        assertThat(responseMap)
                .containsEntry("message",
                        "Person with said id does not exist");
    }

    @Test
    @DisplayName("Update person test")
    void updatePersonTest() throws Exception {
        val personToSave = new PersonSaveDTO(
                10L, "Jovana", "Jovic",
                160, LocalDate.of(1998,10,10),
                11_000,11_000
        );
        val JSONRequest = objectMapper.writeValueAsString(personToSave);

        when(personService.updatePerson(personToSave))
                .thenReturn(personToSave);

        val JSONResponse = mvc.perform(put(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONRequest))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();
        val responsePersonSaveDTO = objectMapper.readValue(JSONResponse,
                PersonSaveDTO.class);

        assertThat(personToSave)
                .isEqualTo(responsePersonSaveDTO);
    }

    @Test
    @DisplayName("Save person bad payload test")
    void updatePersonBadPayloadTest() throws Exception {
        final String badJSONRequest = "This is not real JSON!";

        val JSONResponse = mvc.perform(put(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJSONRequest))
                .andExpect(status().isBadRequest()).andReturn().getResponse()
                .getContentAsString();

        final Map<String, String> responseMap =
                objectMapper.readValue(JSONResponse,
                        new TypeReference<>() {});
        assertThat(responseMap)
                .containsEntry("detail", "Failed to read request")
                .containsEntry("status", "400")
                .containsEntry("title", "Bad Request");
    }

    @Test
    @DisplayName("Save malformed person error test")
    void updatePersonMalformedPersonTest() throws Exception {
        val personToSave = new PersonSaveDTO(
                null, null, "Jovic",
                170,null, 11_000, 11_000
        );

        val JSONRequest = objectMapper.writeValueAsString(personToSave);

        when(personService.updatePerson(personToSave))
                .thenThrow(new PersonNotInitializedException(
                        "Your person may not contain malformed fields."
                ));

        val JSONResponse = mvc.perform(put(requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONRequest))
                .andExpect(status().isNotFound()).andReturn().getResponse()
                .getContentAsString();

        final Map<String, String> responseMap =
                objectMapper.readValue(JSONResponse,
                        new TypeReference<>() {});
        assertThat(responseMap)
                .containsEntry("message",
                        "Your person may not contain malformed fields.");
    }
}
package rs.ac.bg.fon.tps_backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.dto.PersonSaveDTO;
import rs.ac.bg.fon.tps_backend.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(@Qualifier("personTemplateServiceImpl") PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<PersonDisplayDTO>> getAll(){
        return ResponseEntity.ok(personService.getAll());
    }

    @PostMapping
    public ResponseEntity<PersonSaveDTO> savePerson(@Valid @RequestBody
                                                           PersonSaveDTO personSaveDTO)
                                                            throws Exception{
        return ResponseEntity.ok(personService.savePerson(personSaveDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) throws Exception {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<PersonSaveDTO> alterPerson(@Valid @RequestBody
                                                     PersonSaveDTO personSaveDTO) throws Exception {
        return ResponseEntity.ok(
                personService.updatePerson(personSaveDTO));
    }
}

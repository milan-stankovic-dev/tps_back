package rs.ac.bg.fon.tps_backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.tps_backend.dto.PersonDTO;
import rs.ac.bg.fon.tps_backend.service.PersonService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAll(){
        return ResponseEntity.ok(personService.getAll());
    }

    @PostMapping
    public ResponseEntity<PersonDTO> savePerson(@Valid @RequestBody PersonDTO personDTO) throws Exception{
        return ResponseEntity.noContent().build();
    }
}

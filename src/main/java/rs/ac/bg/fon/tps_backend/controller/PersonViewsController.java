package rs.ac.bg.fon.tps_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;
import rs.ac.bg.fon.tps_backend.service.PersonService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("person/views")
public class PersonViewsController {
    private final PersonService personService;

    @Autowired
    public PersonViewsController(@Qualifier("personTemplateServiceImpl")
                                     PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/smederevo")
    public ResponseEntity<List<PersonDisplayDTO>> getAllSmederevci() throws SQLException {
        return ResponseEntity.ok(personService.getAllSmederevci());
    }

    @GetMapping("/adults")
    public ResponseEntity<List<PersonDisplayDTO>> getAllAdults() throws SQLException {
        return ResponseEntity.ok(personService.getAllAdults());
    }
}

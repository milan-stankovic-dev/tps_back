package rs.ac.bg.fon.tps_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.tps_backend.service.PersonService;

@RestController
@RequestMapping("/person/stats")
public class PersonStatsController {
    private final PersonService personService;

    @Autowired
    public PersonStatsController(@Qualifier("personTemplateServiceImpl")
                                     PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/maxHeight")
    public ResponseEntity<Integer> getMaxHeight() {
        return ResponseEntity.ok(personService.getMaxHeight());
    }

    @GetMapping("/averageAgeYears")
    public ResponseEntity<Double> getAverageAgeYears(){
        return ResponseEntity.ok(personService.getAverageAgeYears());
    }
}

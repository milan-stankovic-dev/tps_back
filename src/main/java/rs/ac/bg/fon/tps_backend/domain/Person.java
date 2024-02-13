package rs.ac.bg.fon.tps_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dOB;
    private int ageInMonths;

    @ManyToOne
    @JoinColumn(name = "city_birth_id")
    private City cityOfBirth;

    @ManyToOne
    @JoinColumn(name = "city_residence_id")
    private City cityOfResidence;
}

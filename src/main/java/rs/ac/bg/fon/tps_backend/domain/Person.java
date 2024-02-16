package rs.ac.bg.fon.tps_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import rs.ac.bg.fon.tps_backend.annotation.YearRange;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull
    @Size(max = 30, min = 2, message = "Person's name cannot " +
            "be longer than 30 characters or shorter than 2 characters.")
    private String firstName;

    @Size(max = 30, min = 2, message = "Person's last name cannot " +
            "be longer than 30 characters or shorter than 2 characters.")
    @Value("${person.lastName:Jovanovic}")
    private String lastName;

    @NotNull
    @YearRange(earliestYear = 1950, latestYear = 2005)
    private LocalDate dOB;
    private int ageInMonths;

    @ManyToOne
    @JoinColumn(name = "city_birth_id")
    private City cityOfBirth;

    @ManyToOne
    @JoinColumn(name = "city_residence_id")
    private City cityOfResidence;


}

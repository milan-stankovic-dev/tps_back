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
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 30, min = 2, message = "Person's last name cannot " +
            "be longer than 30 characters or shorter than 2 characters.")
    @Value("${person.lastName:Jovanovic}")
    @Column(name = "last_name")
    private String lastName;

    @Min(value = 70, message = "Person's height must be at least 70 cm.")
    @Max(value = 260, message = "Person's height must be at most 260 cm.")
    @Column(name = "height_in_cm")
    private int heightInCm;

    @NotNull
    @YearRange(earliestYear = 1950, latestYear = 2005)
    @Column(name = "dob")
    private LocalDate dOB;

    @Column(name = "age_in_months")
    private int ageInMonths;

    @ManyToOne
    @JoinColumn(name = "city_birth_id")
    private City cityOfBirth;

    @ManyToOne
    @JoinColumn(name = "city_residence_id")
    private City cityOfResidence;

}

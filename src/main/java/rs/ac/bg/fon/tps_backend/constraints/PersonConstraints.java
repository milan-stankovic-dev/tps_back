package rs.ac.bg.fon.tps_backend.constraints;

import java.time.LocalDate;

public record PersonConstraints(
        int firstNameMinLen,
        int firstNameMaxLen,
        int lastNameMinLen,
        int lastNameMaxLen,
        int heightInCmMin,
        int heightInCmMax,
        LocalDate earliestDOB,
        LocalDate latestDOB

) { }

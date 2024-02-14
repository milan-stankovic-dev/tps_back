package rs.ac.bg.fon.tps_backend.dto;

import java.time.LocalDate;

public record PersonDisplayDTO(
    Long id,
    String firstName,
    String lastName,
    LocalDate dOB,
    int ageInMonths,
    String cityOfBirthName,
    int cityOfBirthPTBR,
    String cityOfResidenceName,
    int cityOfResidencePTBR
) { }

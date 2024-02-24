package rs.ac.bg.fon.tps_backend.dto;

import java.time.LocalDate;

public record PersonDisplayDTO(
    Long id,
    String firstName,
    String lastName,
    int heightInCm,
    LocalDate dOB,
    int ageInMonths,
    String cityOfBirthName,
    int cityOfBirthPPTBR,
    String cityOfResidenceName,
    int cityOfResidencePPTBR
) { }

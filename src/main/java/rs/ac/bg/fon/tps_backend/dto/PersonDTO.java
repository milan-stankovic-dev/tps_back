package rs.ac.bg.fon.tps_backend.dto;

import java.time.LocalDate;

public record PersonDTO(
    Long id,
    String firstName,
    String lastName,
    LocalDate dOB,
    int ageInMonths,
    CityDTO cityOfBirth,
    CityDTO cityOfResidence
) { }

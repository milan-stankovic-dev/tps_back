package rs.ac.bg.fon.tps_backend.dto;

import java.time.LocalDate;

public record PersonSaveDTO(
    Long id,
    String firstName,
    String lastName,
    LocalDate dOB,
    int birthCityCode,
    int residenceCityCode
) { }

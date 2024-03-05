package rs.ac.bg.fon.tps_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.bg.fon.tps_backend.domain.Person;
import rs.ac.bg.fon.tps_backend.dto.PersonDisplayDTO;

import java.sql.ResultSet;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM max_height()")
    Integer getMaxHeight();

    @Query(nativeQuery = true, value = "SELECT * FROM average_age_years()")
    Double getAverageAgeYears();
}

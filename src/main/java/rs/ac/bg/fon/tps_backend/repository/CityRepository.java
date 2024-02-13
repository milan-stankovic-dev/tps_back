package rs.ac.bg.fon.tps_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.tps_backend.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {
}

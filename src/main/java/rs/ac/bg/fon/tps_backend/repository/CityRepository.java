package rs.ac.bg.fon.tps_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.tps_backend.domain.City;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByPtpbr(int i);
}

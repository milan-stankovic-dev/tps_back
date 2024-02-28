package rs.ac.bg.fon.tps_backend.lambda;

import rs.ac.bg.fon.tps_backend.domain.Person;

import java.time.LocalDate;

@FunctionalInterface
public interface UpdateQuery {
    void updateQuery(Person p);
}
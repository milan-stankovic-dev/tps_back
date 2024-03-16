package rs.ac.bg.fon.tps_backend.lambda;

import rs.ac.bg.fon.tps_backend.domain.Person;

@FunctionalInterface
public interface UpdateQuery {
    void updateQuery(Person p);
}
package rs.ac.bg.fon.tps_backend.exception;

public class PersonNotInitializedException extends RuntimeException{
    public PersonNotInitializedException(String message){
        super(message);
    }
}

package rs.ac.bg.fon.tps_backend.exception;

public class UnknownCityException extends RuntimeException{
    public UnknownCityException(String message){
        super(message);
    }
}

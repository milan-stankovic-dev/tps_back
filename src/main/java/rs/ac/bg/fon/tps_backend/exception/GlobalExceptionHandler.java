package rs.ac.bg.fon.tps_backend.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@ControllerAdvice
@Log
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleException(Exception e) {
        if(e == null) {
            throw new IllegalArgumentException("Exception may not be null");
        }

        System.out.println("EXCEPTION OCCURRED.");

        val errorPayload = new ErrorPayload(e.getMessage());
        log.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>(errorPayload, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        if(ex == null){
            throw new IllegalArgumentException("Exception may not be null");
        }

        final Map<String, String> errors = new HashMap<>();
        final List<ObjectError> objectErrors =
                ex.getBindingResult().getAllErrors();
        objectErrors.forEach((error) ->
        {
            final String fieldName = ((FieldError)error).getField();
            final String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}

package rs.ac.bg.fon.tps_backend.exception;

import lombok.val;
import org.apache.coyote.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler;
    @Autowired
    public GlobalExceptionHandlerTest(GlobalExceptionHandler handler) {
        this.handler = handler;
    }

    @Test
    @DisplayName("Handler test for null exception")
    void handleMethodArgumentNotValidTestExNull() {
        assertThatThrownBy(() -> handler.handleMethodArgumentNotValid(
                null, new HttpHeaders(), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exception may not be null");
    }

    @Test
    @DisplayName("Handler test for regular exception")
    void handleMethodArgumentValidTest() {
        MethodArgumentNotValidException exceptionMock = mock(MethodArgumentNotValidException.class);

        BindingResult bindingResultMock = mock(BindingResult.class);
        when(exceptionMock.getBindingResult()).thenReturn(bindingResultMock);

        when(bindingResultMock.getAllErrors()).thenReturn(Collections.singletonList(
                new FieldError("yourObject", "fieldName", "This is a thrown error")));

        final ResponseEntity<Object> responseEntity = handler.handleMethodArgumentNotValid(
                exceptionMock, new HttpHeaders(), HttpStatus.BAD_REQUEST, null);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        final Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("fieldName", "This is a thrown error");

        assertThat(responseEntity.getBody()).isEqualTo(expectedMap);
    }

    @Test
    @DisplayName("Handle exception test null exception")
    void handleExceptionTestForNullException() {
        assertThatThrownBy(() -> handler.handleException(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exception may not be null");
    }

    @Test
    @DisplayName("Handle exception that exists")
    void handleExceptionThatIsNotNull() {
        val expectedResponseEntity =
                new ResponseEntity<>(new ErrorPayload("Example exception"), HttpStatus.NOT_FOUND);

        val actualResponseEntity =
                handler.handleException(new Exception("Example exception"));

        assertThat(expectedResponseEntity)
                .isEqualTo(actualResponseEntity);
    }
}

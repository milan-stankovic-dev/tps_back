package rs.ac.bg.fon.tps_backend.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorPayload {
    private String message;
}

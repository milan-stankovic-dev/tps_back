package rs.ac.bg.fon.tps_backend.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = YearRangeValidator.class)
public @interface YearRange {

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int earliestYear();
    int latestYear();
    String message() default "Date must be between {earliestYear}" +
            "and {latestYear}";
}

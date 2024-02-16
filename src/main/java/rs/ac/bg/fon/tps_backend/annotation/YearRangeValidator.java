package rs.ac.bg.fon.tps_backend.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class YearRangeValidator implements ConstraintValidator<YearRange, LocalDate> {
    private int earliestYear;
    private int latestYear;

    @Override
    public void initialize(YearRange constraintAnnotation) {
        this.earliestYear = constraintAnnotation.earliestYear();
        this.latestYear = constraintAnnotation.latestYear();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {

        if (localDate == null || (localDate.getYear() < earliestYear
                || localDate.getYear() > latestYear)) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Date must be between %d and %d", earliestYear, latestYear)
            ).addConstraintViolation();
            return false;
        }
        return true;
        }
    }


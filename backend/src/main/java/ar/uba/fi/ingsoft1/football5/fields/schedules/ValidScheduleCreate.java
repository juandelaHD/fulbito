package ar.uba.fi.ingsoft1.football5.fields.schedules;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ScheduleCreateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScheduleCreate {
    String message() default "Invalid schedule creation data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
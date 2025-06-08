package ar.uba.fi.ingsoft1.football5.fields.schedules;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleCreateValidator implements ConstraintValidator<ValidScheduleCreate, ScheduleCreateDTO> {

    @Override
    public boolean isValid(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto == null) return false;
        boolean valid = true;

        valid &= validateDateOrder(dto, context);
        valid &= validateStartDateNotPast(dto, context);
        valid &= validateOpeningBeforeClosing(dto, context);
        valid &= validateDaysOfWeek(dto, context);
        valid &= validateTimesForToday(dto, context);
        valid &= validateSlotAndBreakDuration(dto, context);
        valid &= validateSlotDurationRange(dto, context);
        valid &= validateBreakDuration(dto, context);
        valid &= validateMatchingDayInRange(dto, context);

        if (!valid) context.disableDefaultConstraintViolation();
        return valid;
    }

    private boolean validateDateOrder(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.startDate() != null && dto.endDate() != null && dto.startDate().isAfter(dto.endDate())) {
            context.buildConstraintViolationWithTemplate("Start date must be before end date")
                    .addPropertyNode("startDate").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateStartDateNotPast(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.startDate() != null && dto.startDate().isBefore(LocalDate.now())) {
            context.buildConstraintViolationWithTemplate("Start date cannot be in the past")
                    .addPropertyNode("startDate").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateOpeningBeforeClosing(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.openingTime() != null && dto.closingTime() != null && dto.openingTime().isAfter(dto.closingTime())) {
            context.buildConstraintViolationWithTemplate("Opening time must be before closing time")
                    .addPropertyNode("openingTime").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateDaysOfWeek(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.daysOfWeek() == null || dto.daysOfWeek().isEmpty()) {
            context.buildConstraintViolationWithTemplate("At least one day of the week must be selected")
                    .addPropertyNode("daysOfWeek").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateTimesForToday(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.startDate() != null && dto.openingTime() != null && dto.closingTime() != null) {
            if (dto.startDate().isEqual(LocalDate.now())) {
                LocalTime now = LocalTime.now();
                boolean valid = true;
                if (dto.openingTime().isBefore(now)) {
                    context.buildConstraintViolationWithTemplate("Opening time cannot be in the past for today")
                            .addPropertyNode("openingTime").addConstraintViolation();
                    valid = false;
                }
                if (dto.closingTime().isBefore(now)) {
                    context.buildConstraintViolationWithTemplate("Closing time cannot be in the past for today")
                            .addPropertyNode("closingTime").addConstraintViolation();
                    valid = false;
                }
                return valid;
            }
        }
        return true;
    }

    private boolean validateSlotAndBreakDuration(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.openingTime() != null && dto.closingTime() != null &&
                dto.slotDurationMinutes() != null && dto.breakDurationMinutes() != null) {
            int totalMinutes = dto.closingTime().toSecondOfDay() / 60 - dto.openingTime().toSecondOfDay() / 60;
            boolean valid = true;
            if (dto.slotDurationMinutes() + dto.breakDurationMinutes() > totalMinutes) {
                context.buildConstraintViolationWithTemplate("The sum of slot duration and break duration exceeds the available time range")
                        .addPropertyNode("slotDurationMinutes").addConstraintViolation();
                valid = false;
            }
            if (dto.slotDurationMinutes() > totalMinutes) {
                context.buildConstraintViolationWithTemplate("Slot duration does not fit in the available time range")
                        .addPropertyNode("slotDurationMinutes").addConstraintViolation();
                valid = false;
            }
            return valid;
        }
        return true;
    }

    private boolean validateSlotDurationRange(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.slotDurationMinutes() != null && (dto.slotDurationMinutes() <= 0)) {
            context.buildConstraintViolationWithTemplate("Slot duration must be between 30 and 120 minutes")
                    .addPropertyNode("slotDurationMinutes").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateBreakDuration(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.breakDurationMinutes() != null && dto.breakDurationMinutes() < 0) {
            context.buildConstraintViolationWithTemplate("Break duration cannot be negative")
                    .addPropertyNode("breakDurationMinutes").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateMatchingDayInRange(ScheduleCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.startDate() != null && dto.endDate() != null && dto.daysOfWeek() != null && !dto.daysOfWeek().isEmpty()) {
            for (LocalDate date = dto.startDate(); !date.isAfter(dto.endDate()); date = date.plusDays(1)) {
                if (dto.daysOfWeek().contains(date.getDayOfWeek())) {
                    return true;
                }
            }
            context.buildConstraintViolationWithTemplate("No date in the selected range matches the chosen days of the week")
                    .addPropertyNode("daysOfWeek").addConstraintViolation();
            return false;
        }
        return true;
    }
}
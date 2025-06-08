package ar.uba.fi.ingsoft1.football5.fields.schedules;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleGeneratorImpl implements ScheduleGenerator {

    public List<Schedule> generateSchedules(Field field, ScheduleCreateDTO scheduleCreateDTO) {
        List<Schedule> schedules = new ArrayList<>();
        LocalDate currentDate = scheduleCreateDTO.startDate();
        LocalDate endDate = scheduleCreateDTO.endDate();
        LocalTime opening = scheduleCreateDTO.openingTime();
        LocalTime closing = scheduleCreateDTO.closingTime();
        Integer slotMinutes = scheduleCreateDTO.slotDurationMinutes();
        Integer breakMinutes = scheduleCreateDTO.breakDurationMinutes();
        List<DayOfWeek> daysOfWeek = scheduleCreateDTO.daysOfWeek();

        while (!currentDate.isAfter(endDate)) {
            if (daysOfWeek.contains(currentDate.getDayOfWeek())) {
                LocalTime cursor = opening;

                while (!cursor.plusMinutes(slotMinutes).isAfter(closing)) {
                    Schedule schedule = new Schedule(
                            field,
                            currentDate,
                            cursor,
                            cursor.plusMinutes(slotMinutes),
                            ScheduleStatus.AVAILABLE
                    );
                    schedules.add(schedule);
                    cursor = cursor.plusMinutes(slotMinutes + breakMinutes);
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        return schedules;
    }
}

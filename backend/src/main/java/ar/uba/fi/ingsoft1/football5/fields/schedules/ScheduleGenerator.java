package ar.uba.fi.ingsoft1.football5.fields.schedules;

import ar.uba.fi.ingsoft1.football5.fields.Field;

import java.util.List;

public interface ScheduleGenerator {

    List<Schedule> generateSchedules(Field field, ScheduleCreateDTO scheduleCreateDTO);

}


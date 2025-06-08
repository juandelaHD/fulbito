package ar.uba.fi.ingsoft1.football5.fields.schedules;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final FieldService fieldService;
    private final ScheduleGeneratorImpl scheduleGenerator;


    @Autowired
    ScheduleService(ScheduleRepository scheduleRepository, FieldService fieldService,
                    ScheduleGeneratorImpl scheduleGenerator) {
        this.scheduleRepository = scheduleRepository;
        this.fieldService = fieldService;
        this.scheduleGenerator = scheduleGenerator;
    }

    public List<ScheduleDTO> createSchedule(Long fieldId, ScheduleCreateDTO scheduleCreate, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        if (scheduleCreate.startDate().isAfter(scheduleCreate.endDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (scheduleCreate.startDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (scheduleCreate.openingTime().isAfter(scheduleCreate.closingTime())) {
            throw new IllegalArgumentException("OpeningTime must be before ClosingTime");
        }
        if (scheduleCreate.slotDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Slot duration must be positive");
        }
        if (scheduleCreate.breakDurationMinutes() < 0) {
            throw new IllegalArgumentException("Break duration must be non-negative");
        }
        if (scheduleCreate.daysOfWeek().isEmpty()) {
            throw new IllegalArgumentException("At least one day of the week must be selected");
        }
        if (scheduleCreate.startDate().isAfter(scheduleCreate.endDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (scheduleCreate.startDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (scheduleCreate.openingTime().isAfter(scheduleCreate.closingTime())) {
            throw new IllegalArgumentException("Opening time must be before closing time");
        }
        int totalMinutes = scheduleCreate.closingTime().toSecondOfDay() / 60 -
                scheduleCreate.openingTime().toSecondOfDay() / 60;
        if (scheduleCreate.slotDurationMinutes() + scheduleCreate.breakDurationMinutes() > totalMinutes) {
            throw new IllegalArgumentException("The sum of slot duration and break duration must not exceed the total available time in the day");
        }
        Field field = fieldService.loadFieldById(fieldId);
        fieldService.validateOwnership(field, userDetails);

        List<Schedule> schedules = scheduleGenerator.generateSchedules(field, scheduleCreate);
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("No schedules generated. Check the input parameters.");
        }
        schedules.forEach(schedule -> {
            schedule = scheduleRepository.save(schedule);
            field.getSchedules().add(schedule);
        });

        return schedules.stream()
                .map(ScheduleDTO::new)
                .toList();
    }

    public Page<ScheduleDTO> getSchedulesByFieldId(Long fieldId, Pageable pageable) throws ItemNotFoundException {
        Field field = fieldService.loadFieldById(fieldId);

        Page<Schedule> schedulePage = scheduleRepository.findByField(field, pageable);
        return schedulePage.map(ScheduleDTO::new);
    }
}

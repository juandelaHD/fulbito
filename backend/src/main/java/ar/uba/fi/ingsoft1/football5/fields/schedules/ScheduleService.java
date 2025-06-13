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
import java.time.LocalTime;
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

    public Page<ScheduleDTO> getAvailableSchedulesByFieldId(Long fieldId, Pageable pageable) throws ItemNotFoundException {
        Field field = fieldService.loadFieldById(fieldId);
        Page<Schedule> schedulePage = scheduleRepository.findByFieldAndStatus(field, ScheduleStatus.AVAILABLE, pageable);
        return schedulePage.map(ScheduleDTO::new);
    }

    public List<ScheduleDTO> getScheduleSlotsByFieldAndDate(Long fieldId, LocalDate date) throws ItemNotFoundException {
        Field field = fieldService.loadFieldById(fieldId);
        List<Schedule> schedules = scheduleRepository.findByFieldAndDate(field, date);
        return schedules.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.AVAILABLE)
                .map(s -> new ScheduleDTO(
                        s.getId(),
                        s.getDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getStatus()
                ))
                .toList();
    }

    public ScheduleDTO markAsReserved(Field Field, LocalDate date, LocalTime startTime, LocalTime endTime) throws ItemNotFoundException {
        Schedule schedule = scheduleRepository.findByFieldAndDateAndStartTimeAndEndTime(Field, date, startTime, endTime)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Schedule", Field.getId()));

        if (schedule.getStatus() != ScheduleStatus.AVAILABLE) {
            throw new IllegalArgumentException("Schedule is not available for booking.");
        }

        schedule.setStatus(ScheduleStatus.RESERVED);
        scheduleRepository.save(schedule);

        return new ScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()

        );
    }

    public ScheduleDTO markAsAvailable(Field Field, LocalDate date, LocalTime startTime, LocalTime endTime) throws ItemNotFoundException {
        Schedule schedule = scheduleRepository.findByFieldAndDateAndStartTimeAndEndTime(Field, date, startTime, endTime)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Schedule", Field.getId()));

        // Check if the schedule is reserved before marking it as available
        if (schedule.getStatus() != ScheduleStatus.RESERVED) {
            throw new IllegalArgumentException("Schedule is not reserved and cannot be marked as available.");
        }

        schedule.setStatus(ScheduleStatus.AVAILABLE);
        scheduleRepository.save(schedule);

        return new ScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }

    public ScheduleDTO markAsBlocked(Field Field, LocalDate date, LocalTime startTime, LocalTime endTime) throws ItemNotFoundException {
        Schedule schedule = scheduleRepository.findByFieldAndDateAndStartTimeAndEndTime(Field, date, startTime, endTime)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Schedule", Field.getId()));

        if (schedule.getStatus() != ScheduleStatus.AVAILABLE) {
            throw new IllegalArgumentException("Schedule is not available and cannot be marked as blocked.");
        }

        schedule.setStatus(ScheduleStatus.BLOCKED);
        scheduleRepository.save(schedule);

        return new ScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }
}

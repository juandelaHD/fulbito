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

    private static final String SCHEDULE_ITEM = "schedule";
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
        validateGeneratedSchedules(schedules);

        schedules.forEach(schedule -> {
            schedule = scheduleRepository.save(schedule);
            field.getSchedules().add(schedule);
        });

        return schedules.stream()
                .map(ScheduleDTO::new)
                .toList();
    }

    private void validateGeneratedSchedules(List<Schedule> schedules) {
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("No schedules generated. Check the input parameters.");
        }
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
                .map(s -> new ScheduleDTO(
                        s.getId(),
                        s.getDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getStatus()
                ))
                .toList();
    }

    public ScheduleDTO markAsReserved(Field field, LocalDate date, LocalTime startTime, LocalTime endTime) throws ItemNotFoundException {
        Schedule schedule = scheduleRepository.findByFieldAndDateAndStartTimeAndEndTime(field, date, startTime, endTime)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(SCHEDULE_ITEM, field.getId()));

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

    public void markAsAvailable(Field field, LocalDate date, LocalTime startTime, LocalTime endTime) throws ItemNotFoundException {
        Schedule schedule = scheduleRepository.findByFieldAndDateAndStartTimeAndEndTime(field, date, startTime, endTime)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(SCHEDULE_ITEM, field.getId()));

        // Check if the schedule is reserved before marking it as available
        if (schedule.getStatus() != ScheduleStatus.RESERVED) {
            throw new IllegalArgumentException("Schedule is not reserved and cannot be marked as available.");
        }

        schedule.setStatus(ScheduleStatus.AVAILABLE);
        scheduleRepository.save(schedule);
    }

    public ScheduleDTO markAsBlocked(Field field, LocalDate date, LocalTime startTime, LocalTime endTime) throws ItemNotFoundException {
        Schedule schedule = scheduleRepository.findByFieldAndDateAndStartTimeAndEndTime(field, date, startTime, endTime)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(SCHEDULE_ITEM, field.getId()));

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

    public ScheduleDTO updateScheduleStatus(Long fieldId, Long scheduleId, String status, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {

        // Validar que el usuario sea admin del campo si es necesario
        Field field = fieldService.loadFieldById(fieldId);
        if (!fieldService.isFieldAdmin(fieldId, userDetails)) {
            throw new IllegalArgumentException("Only field admins can update schedule statuses.");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ItemNotFoundException(SCHEDULE_ITEM, scheduleId));

        if (!schedule.getField().getId().equals(fieldId)) {
            throw new IllegalArgumentException("The schedule does not belong to the specified field.");
        }

        ScheduleStatus newStatus;
        try {
            newStatus = ScheduleStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("State not valid: " + status);
        }

        if (newStatus == ScheduleStatus.RESERVED) {
            throw new IllegalArgumentException("The status RESERVED cannot be set directly.");
        }

        if (newStatus == ScheduleStatus.AVAILABLE) {
            if (schedule.getStatus() != ScheduleStatus.RESERVED && schedule.getStatus() != ScheduleStatus.BLOCKED) {
                throw new IllegalArgumentException("It can only be marked as AVAILABLE if it is BLOCKED.");
            }
        } else if (newStatus == ScheduleStatus.BLOCKED) {
            if (schedule.getStatus() != ScheduleStatus.AVAILABLE) {
                throw new IllegalArgumentException("It can only be marked as BLOCKED if it is AVAILABLE.");
            }
        }

        schedule.setStatus(newStatus);
        scheduleRepository.save(schedule);

        return new ScheduleDTO(
                schedule.getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }

    public void deleteSchedule(Long fieldId, Long scheduleId, JwtUserDetails userDetails) throws ItemNotFoundException, IllegalArgumentException {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ItemNotFoundException("schedule", scheduleId));

        // Verificar que el schedule pertenece a la cancha indicada
        if (!schedule.getField().getId().equals(fieldId)) {
            throw new IllegalArgumentException("The schedule does not belong to the specified field.");
        }

        // Verificar que el usuario autenticado es el dueño de la cancha
        if (!schedule.getField().getOwner().getUsername().equalsIgnoreCase(userDetails.username())) {
            throw new IllegalArgumentException("The authenticated user is not the owner of the field.");
        }

        // No eliminar si está reservado
        if (schedule.getStatus() == ScheduleStatus.RESERVED) {
            throw new IllegalArgumentException("The schedule cannot be deleted because it is reserved.");
        }

        scheduleRepository.delete(schedule);
    }
}

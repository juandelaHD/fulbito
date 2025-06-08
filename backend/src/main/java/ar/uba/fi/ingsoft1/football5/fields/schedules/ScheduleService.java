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
            throws ItemNotFoundException {
        Field field = fieldService.loadFieldById(fieldId);
        fieldService.validateOwnership(field, userDetails);

        List<Schedule> schedules = scheduleGenerator.generateSchedules(field, scheduleCreate);
        schedules.forEach(schedule -> {
            schedule = scheduleRepository.save(schedule);
            field.getSchedules().add(schedule);
        });

        return schedules.stream()
                .map(ScheduleDTO::new)
                .toList();
    }

    public Page<List<ScheduleDTO>> getSchedulesByFieldId(Long fieldId, Pageable pageable) throws ItemNotFoundException {
//        Field field = fieldService.loadFieldById(fieldId);
//
//        Page<Schedule> schedulePage = scheduleRepository.findByField(field, pageable);
//        if (schedulePage.isEmpty()) {
//            return Page.empty(pageable);
//        }
//        return schedulePage.map(schedules -> schedules.stream()
//                .map(ScheduleDTO::new)
//                .toList());
        return Page.empty();
    }
}

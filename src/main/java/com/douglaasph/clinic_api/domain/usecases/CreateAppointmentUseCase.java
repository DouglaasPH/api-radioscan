package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.inbound.CreateAppointmentUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetEmployeeByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateAppointmentUseCase implements CreateAppointmentUseCasePort {
    private final SaveAppointmentAdapterPort saveAppointmentAdapterPort;
    private final GetEmployeeByIdAdapterPort getEmployeeByIdAdapterPort;

    public CreateAppointmentUseCase(SaveAppointmentAdapterPort saveAppointmentAdapterPort, GetEmployeeByIdAdapterPort getEmployeeByIdAdapterPort) {
        this.saveAppointmentAdapterPort = saveAppointmentAdapterPort;
        this.getEmployeeByIdAdapterPort = getEmployeeByIdAdapterPort;
    }

    @Override
    public Appointment execute(Long employee_id, LocalDateTime dateHour, AppointmentType type) {
        Employee employee = getEmployeeByIdAdapterPort.get(employee_id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employee_id));

        // TECHNICAL CODE IS 1 === EXAM_CAPTURE CODE IS 1
        // DOCTOR CODE IS 2 === REPORT_REVIEW CODE IS 2
        if (type.getCode() != employee.getPosition().getCode()) {
            throw new BusinessRuleException("Employee's position incompatible with the type of inquiry.");
        }

        Appointment appointment = new Appointment(
                null,
                employee,
                null,
                dateHour,
                AppointmentStatus.AVAILABLE,
                type,
                null
        );
        return saveAppointmentAdapterPort.save(appointment);
    }
}

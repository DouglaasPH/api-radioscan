package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaMapper;
import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveAppointmentAdapter implements SaveAppointmentAdapterPort {
    private final AppointmentRepository appointmentRepository;

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentJpaEntity entity = new AppointmentJpaEntity(
                appointment.getId(),
                EmployeeJpaMapper.toEntity(appointment.getEmployee()),
                PatientJpaMapper.toEntity(appointment.getPatient()),
                appointment.getDateHour(),
                appointment.getStatus(),
                appointment.getType()
        );
        return AppointmentJPaMapper.toDomain(appointmentRepository.save(entity));
    }
}

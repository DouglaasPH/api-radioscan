package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportJpaMapper;
import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;

public class AppointmentJPaMapper {
    public static Appointment toDomain(AppointmentJpaEntity entity) {
        return new Appointment(
                entity.getId(),
                EmployeeJpaMapper.toDomain(entity.getEmployee()),
                entity.getPatient() != null ?PatientJpaMapper.toDomain(entity.getPatient()) : null,
                entity.getDateHour(),
                AppointmentStatus.valueOf(entity.getAppointmentStatus()),
                AppointmentType.valueOf(entity.getAppointmentType()),
                entity.getXRayReport() != null ? XRayReportJpaMapper.toDomain(entity.getXRayReport()) : null
        );
    }

    public static AppointmentJpaEntity toEntity(Appointment appointment) {
        return new AppointmentJpaEntity(
                appointment.getId(),
                EmployeeJpaMapper.toEntity(appointment.getEmployee()),
                appointment.getPatient() != null ? PatientJpaMapper.toEntity(appointment.getPatient()) : null,
                appointment.getDateHour(),
                appointment.getStatus(),
                appointment.getType(),
                appointment.getXRayReport() != null ? XRayReportJpaMapper.toEntity(appointment.getXRayReport()) : null
        );
    }
}

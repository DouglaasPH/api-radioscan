package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "appointments")
public class AppointmentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeJpaEntity employee;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = true)
    private PatientJpaEntity patient;

    @Column(name = "date_hour", nullable = false)
    private LocalDateTime dateHour;

    @Column(nullable = false)
    private Integer appointmentStatus;

    @Column(nullable = false)
    private Integer appointmentType;

    @ManyToOne
    @JoinColumn(name = "x_ray_report_id", nullable = true)
    private XRayReportJpaEntity xRayReport;

    public AppointmentJpaEntity(Long id, EmployeeJpaEntity employee, PatientJpaEntity patient, LocalDateTime dateHour, AppointmentStatus appointmentStatus, AppointmentType appointmentType) {
        this.id = id;
        this.employee = employee;
        this.patient = patient;
        this.dateHour = dateHour;
        setStatus(appointmentStatus);
        setType(appointmentType);
    }

    public AppointmentStatus getStatus() {
        return AppointmentStatus.valueOf(appointmentStatus);
    }

    public void setStatus(AppointmentStatus appointmentStatus) {
        if (appointmentStatus != null) {
            this.appointmentStatus = appointmentStatus.getCode();
        }
    }

    public AppointmentType getType() {
        return AppointmentType.valueOf(appointmentType);
    }

    public void setType(AppointmentType appointmentType) {
        if (appointmentType != null) {
            this.appointmentType = appointmentType.getCode();
        }
    }
}

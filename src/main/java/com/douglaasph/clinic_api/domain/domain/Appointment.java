package com.douglaasph.clinic_api.domain.domain;

import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;

import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Employee employee;
    private Patient patient;
    private LocalDateTime dateHour;
    private Integer appointmentStatus;
    private Integer appointmentType;
    private XRayReport xRayReport;

    public Appointment() {}

    public Appointment(Long id, Employee employee, Patient patient, LocalDateTime dateHour, AppointmentStatus appointmentStatus, AppointmentType appointmentType, XRayReport xRayReport) {
        this.id = id;
        this.employee = employee;
        this.patient = patient;
        this.dateHour = dateHour;
        setStatus(appointmentStatus);
        setType(appointmentType);
        this.xRayReport = xRayReport;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getDateHour() {
        return dateHour;
    }

    public void setDateHour(LocalDateTime dateHour) {
        this.dateHour = dateHour;
    }

    public XRayReport getXRayReport() {
        return xRayReport;
    }

    public void setXRayReport(XRayReport xRayReport) {
        this.xRayReport = xRayReport;
    }
}

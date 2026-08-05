package com.douglaasph.clinic_api.adapter.inbound.controller.appointment.request;

import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateAppointmentRequest {
    @NotNull(message = "The employee_id cannot be null.")
    private Long employee_id;

    @NotNull(message = "The date and hour cannot be null.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateHour;

    @NotNull(message = "The type cannot be null.")
    private AppointmentType type;

    public CreateAppointmentRequest() {
    }

    public CreateAppointmentRequest(Long employee_id, LocalDateTime dateHour, AppointmentType type) {
        this.employee_id = employee_id;
        this.dateHour = dateHour;
        this.type = type;
    }

    public Long getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(Long employee_id) {
        this.employee_id = employee_id;
    }

    public LocalDateTime getDateHour() {
        return dateHour;
    }

    public void setDateHour(LocalDateTime dateHour) {
        this.dateHour = dateHour;
    }

    public AppointmentType getType() {
        return type;
    }

    public void setType(AppointmentType type) {
        this.type = type;
    }
}

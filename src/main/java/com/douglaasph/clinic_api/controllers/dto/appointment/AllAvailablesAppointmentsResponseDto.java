package com.douglaasph.clinic_api.controllers.dto.appointment;

import java.time.LocalDateTime;

public record AllAvailablesAppointmentsResponseDto(
        Long id,
        LocalDateTime dateHour,
        Integer appointmentType,   // Código numérico retornado direto pro frontend
        String employeeName
) {}

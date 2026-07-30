package com.douglaasph.clinic_api.controllers.dto.appointment;

import java.time.LocalDateTime;

public record PatientEmployeeAppointmentResponseDto(
        Long id,
        Integer appointmentStatus,
        Integer appointmentType,
        String employeeName,
        LocalDateTime dateHour,
        Long xRayReportId,
        Boolean isReleasedToPatientXRayReport, // Boolean -> true, false or null.
        Boolean isPendingProcessing
) {}

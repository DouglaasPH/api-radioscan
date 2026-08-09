package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.inbound.BookReportReviewUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.*;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookReportReviewUseCase implements BookReportReviewUseCasePort {
    private final SaveAppointmentAdapterPort saveAppointmentAdapterPort;
    private final GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final GetXRayReportByIdAdapterPort getXRayReportByIdAdapterPort;
    private final SaveXRayReportAdapterPort saveXRayReportAdapterPort;

    public BookReportReviewUseCase(SaveAppointmentAdapterPort saveAppointmentAdapterPort, GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort, GetUserByEmailAdapterPort getUserByEmailAdapterPort, GetXRayReportByIdAdapterPort getXRayReportByIdAdapterPort, SaveXRayReportAdapterPort saveXRayReportAdapterPort) {
        this.saveAppointmentAdapterPort = saveAppointmentAdapterPort;
        this.getAppointmentByIdAdapterPort = getAppointmentByIdAdapterPort;
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.getXRayReportByIdAdapterPort = getXRayReportByIdAdapterPort;
        this.saveXRayReportAdapterPort = saveXRayReportAdapterPort;
    }

    @Override
    public Appointment execute(Long appointmentId, Long xRayReportId, String patientEmail) throws AppointmentConflictException {
        User user = getUserByEmailAdapterPort.findByEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", patientEmail));

        if (user.getPatient() == null) {
            throw new BusinessRuleException("Authenticated user does not have an associated patient profile.");
        }

        Patient patient = user.getPatient();

        Appointment appointment = getAppointmentByIdAdapterPort.get(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (appointment.getType() != AppointmentType.REPORT_REVIEW) {
            throw new AppointmentConflictException("This appointment is not of the REPORT_REVIEW type.");
        }

        if (appointment.getPatient() != null) {
            throw new AppointmentConflictException("This time slot is already reserved by another patient.");
        }
        if (appointment.getStatus() != AppointmentStatus.AVAILABLE) {
            throw new AppointmentConflictException("This time slot is not available for scheduling.");
        }

        XRayReport xRayReport = getXRayReportByIdAdapterPort.get(xRayReportId)
                .orElseThrow(() -> new ResourceNotFoundException("XRayReport", "id", xRayReportId));
        xRayReport.setProcessingStatus(3); // AWAITING_VALIDATION_BY_DOCTOR
        appointment.setXRayReport(xRayReport);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        saveXRayReportAdapterPort.save(xRayReport);
        return saveAppointmentAdapterPort.save(appointment);
    }
}

package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.results.UrlPressignedS3Result;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.ProcessingStatus;
import com.douglaasph.clinic_api.domain.ports.inbound.StartExamCaptureUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.*;
import com.douglaasph.clinic_api.exceptions.AccessDeniedException;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StartExamCaptureUseCase implements StartExamCaptureUseCasePort {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;
    private final SaveAppointmentAdapterPort saveAppointmentAdapterPort;
    private final SaveXRayReportAdapterPort saveXRayReportAdapterPort;
    private final FileStorageAdapterPort fileStorageAdapterPort;

    public StartExamCaptureUseCase(GetUserByEmailAdapterPort getUserByEmailAdapterPort, GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort, SaveAppointmentAdapterPort saveAppointmentAdapterPort, SaveXRayReportAdapterPort saveXRayReportAdapterPort, FileStorageAdapterPort fileStorageAdapterPort) {
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.getAppointmentByIdAdapterPort = getAppointmentByIdAdapterPort;
        this.saveAppointmentAdapterPort = saveAppointmentAdapterPort;
        this.saveXRayReportAdapterPort = saveXRayReportAdapterPort;
        this.fileStorageAdapterPort = fileStorageAdapterPort;
    }

    @Override
    public UrlPressignedS3Result execute(Long appointmentId, String email) throws AppointmentConflictException {
        User user = getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (user.getEmployee() == null) {
            throw new AccessDeniedException("Logged user is not registered as an employee.");
        }

        Long loggedEmployeeId = user.getEmployee().getId();

        Appointment appointment = getAppointmentByIdAdapterPort.get(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (appointment.getEmployee() == null || !appointment.getEmployee().getId().equals(loggedEmployeeId)) {
            throw new AccessDeniedException("This appointment is not assigned to you.");
        }

        if (appointment.getType() != AppointmentType.EXAM_CAPTURE) {
            throw new BusinessRuleException("This appointment scheduling is not intended for the collection of test samples.");
        }

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new AppointmentConflictException("The scheduled task must have the status SCHEDULED in order to be started.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        saveAppointmentAdapterPort.save(appointment);

        return new UrlPressignedS3Result(
                createReportAndGenerateUploadUrl(appointment)
        );
    }

    private String createReportAndGenerateUploadUrl(Appointment appointment) {
        String s3Key = "exams/" + UUID.randomUUID() + ".png";

        XRayReport report = new XRayReport(null,
                null,
                s3Key,
                ProcessingStatus.PROCESSED_BY_IA.getCode(),
                null,
                null,
                false,
                List.of()
        );
        XRayReport savedReport = saveXRayReportAdapterPort.save(report);

        appointment.setXRayReport(savedReport);
        saveAppointmentAdapterPort.save(appointment);

        return fileStorageAdapterPort.generateUploadUrl(s3Key);
    }
}

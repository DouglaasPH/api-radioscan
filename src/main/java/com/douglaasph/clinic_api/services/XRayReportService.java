package com.douglaasph.clinic_api.services;

import com.douglaasph.clinic_api.config.aws.StorageGateway;
import com.douglaasph.clinic_api.controllers.dto.xRayReport.RequestDownloadResponseDto;
import com.douglaasph.clinic_api.exceptions.ReportNotReleasedException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import com.douglaasph.clinic_api.models.entities.Appointment;
import com.douglaasph.clinic_api.models.entities.XRayReport;
import com.douglaasph.clinic_api.models.entities.enums.ProcessingStatus;
import com.douglaasph.clinic_api.repositories.AppointmentRepository;
import com.douglaasph.clinic_api.repositories.UserRepository;
import com.douglaasph.clinic_api.repositories.XRayReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class XRayReportService {
    @Autowired
    private XRayReportRepository xRayReportRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageGateway storageGateway;

    @Transactional
    public String createReportAndGenerateUploadUrl(Appointment appointment) {
        String s3Key = "exams/" + UUID.randomUUID() + ".png";

        XRayReport report = new XRayReport(null,
                null,
                s3Key,
                ProcessingStatus.PROCESSED_BY_IA.getCode(),
                null,
                null,
                false,
                List.of(appointment));
        xRayReportRepository.save(report);

        return storageGateway.generatePresignedUploadUrl(s3Key);
    }

    @Transactional
    public XRayReport reviewDoctor(Long appointmentId, String finalDoctorDiagnosis) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        XRayReport xRayReport = xRayReportRepository.findByAppointmentId(appointmentId).orElseThrow(() -> new ResourceNotFoundException("XRayReport", "id of the appointment", appointmentId));
        xRayReport.setFinalMedicalDiagnosis(finalDoctorDiagnosis);
        xRayReport.setProcessingStatus(4);
        appointment.setAppointmentStatus(4);
        xRayReport.setReleasedToPatient(true);

        appointmentRepository.save(appointment);
        xRayReportRepository.save(xRayReport);

        return xRayReport;
    }

    @Transactional
    public List<XRayReport> findAllByPatientIdAndReleasedToPatientTrue(String email) {
        Long patientId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();
        return xRayReportRepository.findAllByAppointment_Patient_IdAndReleasedToPatientTrue(patientId);
    }

    @Transactional
    public RequestDownloadResponseDto getExamDownloadUrl(Long reportId) throws ReportNotReleasedException {
        XRayReport report = xRayReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId));

        // Business Rule: The exam must be released to the patient.
        if (!report.isReleasedToPatient()) {
            throw new ReportNotReleasedException("This exam result has not been released by the doctor yet.");
        }

        String downloadUrl = storageGateway.generatePresignedDownloadUrl(report.getS3Key());
        return new RequestDownloadResponseDto(downloadUrl);
    }

}

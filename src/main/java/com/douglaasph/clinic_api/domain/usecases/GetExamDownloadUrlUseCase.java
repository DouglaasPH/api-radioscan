package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.inbound.GetExamDownloadUrlUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.FileStorageAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetXRayReportByIdAdapterPort;
import com.douglaasph.clinic_api.exceptions.ReportNotReleasedException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetExamDownloadUrlUseCase implements GetExamDownloadUrlUseCasePort {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final GetXRayReportByIdAdapterPort getXRayReportByIdAdapterPort;
    private final FileStorageAdapterPort fileStorageAdapterPort;

    public GetExamDownloadUrlUseCase(GetUserByEmailAdapterPort getUserByEmailAdapterPort, GetXRayReportByIdAdapterPort getXRayReportByIdAdapterPort, FileStorageAdapterPort fileStorageAdapterPort) {
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.getXRayReportByIdAdapterPort = getXRayReportByIdAdapterPort;
        this.fileStorageAdapterPort = fileStorageAdapterPort;
    }

    @Override
    public String execute(Long reportId, String email) {
        User user = getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        XRayReport report = getXRayReportByIdAdapterPort.get(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId));

        // Business Rule: The exam must be released to the patient.
        if (!report.isReleasedToPatient() && user.getRole() == Roles.PATIENT) {
            throw new ReportNotReleasedException("This exam result has not been released by the doctor yet.");
        }

        return fileStorageAdapterPort.generateDownloadUrl(report.getS3Key());
    }
}

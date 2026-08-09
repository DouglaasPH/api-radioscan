package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.ports.inbound.DoctorReviewExamUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveXRayReportAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DoctorReviewExamUseCase implements DoctorReviewExamUseCasePort {
    private final GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;
    private final SaveAppointmentAdapterPort saveAppointmentAdapterPort;
    private final SaveXRayReportAdapterPort saveXRayReportAdapterPort;

    public DoctorReviewExamUseCase(GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort, SaveAppointmentAdapterPort saveAppointmentAdapterPort, SaveXRayReportAdapterPort saveXRayReportAdapterPort) {
        this.getAppointmentByIdAdapterPort = getAppointmentByIdAdapterPort;
        this.saveAppointmentAdapterPort = saveAppointmentAdapterPort;
        this.saveXRayReportAdapterPort = saveXRayReportAdapterPort;
    }

    @Override
    public XRayReport execute(Long appointmentId, String finalDoctorDiagnosis) {
        Appointment appointment = getAppointmentByIdAdapterPort.get(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        XRayReport xRayReport = appointment.getXRayReport();

        if (xRayReport == null) {
            throw  new ResourceNotFoundException("XRayReport", "id of the appointment", appointmentId);
        }

        xRayReport.setFinalMedicalDiagnosis(finalDoctorDiagnosis);
        xRayReport.setProcessingStatus(4);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        xRayReport.setReleasedToPatient(true);

        saveAppointmentAdapterPort.save(appointment);
        return saveXRayReportAdapterPort.save(xRayReport);
    }
}

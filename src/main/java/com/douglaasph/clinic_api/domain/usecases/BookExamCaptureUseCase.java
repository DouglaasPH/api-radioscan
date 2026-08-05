package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.inbound.BookExamCaptureUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookExamCaptureUseCase implements BookExamCaptureUseCasePort {
    private final SaveAppointmentAdapterPort saveAppointmentAdapterPort;
    private final GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    public BookExamCaptureUseCase(SaveAppointmentAdapterPort saveAppointmentAdapterPort, GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort, GetUserByEmailAdapterPort getUserByEmailAdapterPort) {
        this.saveAppointmentAdapterPort = saveAppointmentAdapterPort;
        this.getAppointmentByIdAdapterPort = getAppointmentByIdAdapterPort;
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
    }

    @Override
    public Appointment execute(Long appointmentId, String patientEmail) throws AppointmentConflictException {
        User user = getUserByEmailAdapterPort.findByEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", patientEmail));

        if (user.getPatient() == null) {
            throw new BusinessRuleException("Authenticated user does not have an associated patient profile.");
        }

        Patient patient = user.getPatient();

        Appointment appointment = getAppointmentByIdAdapterPort.get(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (appointment.getType() != AppointmentType.EXAM_CAPTURE) {
            throw new AppointmentConflictException("This appointment is not of the EXAM_CAPTURE type.");
        }

        if (appointment.getPatient() != null) {
            throw new AppointmentConflictException("This time slot is already reserved by another patient.");
        }
        if (appointment.getStatus() != AppointmentStatus.AVAILABLE) {
            throw new AppointmentConflictException("This time slot is not available for scheduling.");
        }

        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return saveAppointmentAdapterPort.save(appointment);
    }
}

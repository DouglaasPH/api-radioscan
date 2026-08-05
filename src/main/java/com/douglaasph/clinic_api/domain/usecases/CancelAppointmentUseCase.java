package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.inbound.CancelAppointmentUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.exceptions.AccessDeniedException;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CancelAppointmentUseCase implements CancelAppointmentUseCasePort {
    private final GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final SaveAppointmentAdapterPort saveAppointmentAdapterPort;

    public CancelAppointmentUseCase(GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort, GetUserByEmailAdapterPort getUserByEmailAdapterPort, SaveAppointmentAdapterPort saveAppointmentAdapterPort) {
        this.getAppointmentByIdAdapterPort = getAppointmentByIdAdapterPort;
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.saveAppointmentAdapterPort = saveAppointmentAdapterPort;
    }

    @Override
    public Appointment execute(Long appointmentId, String email) {
        Appointment appointment = getAppointmentByIdAdapterPort.get(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (appointment.getStatus() == AppointmentStatus.CANCELED) {
            throw new BusinessRuleException("Appointment is already canceled.");
        }

        User user = getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (user.getRole().equals(Roles.PATIENT)) {
            if (user.getPatient() == null || appointment.getPatient() == null ||
                    !Objects.equals(appointment.getPatient().getId(), user.getPatient().getId())) {
                throw new AccessDeniedException("You do not have permission to cancel this appointment.");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(appointment.getDateHour().minusHours(24))) {
            throw new BusinessRuleException("The appointment can only be cancelled with 24 hours' notice.");
        }

        if (appointment.getxRayReport() != null) {
            appointment.getxRayReport().setProcessingStatus(2); // PROCESSED_BY_IA
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        return saveAppointmentAdapterPort.save(appointment);
    }
}

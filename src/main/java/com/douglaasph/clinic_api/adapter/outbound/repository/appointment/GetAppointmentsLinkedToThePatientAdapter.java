package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentsLinkedToThePatientAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAppointmentsLinkedToThePatientAdapter implements GetAppointmentsLinkedToThePatientAdapterPort {
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentsLinkedToThePatientResult> get(String patientEmail) {
        return appointmentRepository.findByPatientEmail(patientEmail);
    }
}

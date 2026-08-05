package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAppointmentByIdAdapter implements GetAppointmentByIdAdapterPort {
    private final AppointmentRepository appointmentRepository;

    @Override
    public Optional<Appointment> get(Long appointmentId) {
        return appointmentRepository.findById(appointmentId).map(AppointmentJPaMapper::toDomain);
    }
}

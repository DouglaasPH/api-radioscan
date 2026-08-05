package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.domain.results.AvailabilitiesAppointmentsResult;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAllAvailabilitiesAppointmentsAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class GetAllAvailabilitiesAppointmentsAdapter implements GetAllAvailabilitiesAppointmentsAdapterPort {
    private final AppointmentRepository appointmentRepository;

    @Override
    public Page<AvailabilitiesAppointmentsResult> get(LocalDate date, AppointmentType appointmentType, Integer page) {
        PageRequest pageable = PageRequest.of(page, 4);
        return appointmentRepository.findAllAvailablesAppointments(
                appointmentType.getCode(),
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX),
                pageable
        );
    }
}

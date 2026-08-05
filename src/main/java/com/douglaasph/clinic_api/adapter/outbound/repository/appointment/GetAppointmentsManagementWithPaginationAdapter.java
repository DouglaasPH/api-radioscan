package com.douglaasph.clinic_api.adapter.outbound.repository.appointment;

import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentsManagementWithPaginationAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAppointmentsManagementWithPaginationAdapter implements GetAppointmentsManagementWithPaginationAdapterPort {
    private final AppointmentRepository appointmentRepository;

    @Override
    public Page<AppointmentsManagementResult> get(AppointmentStatus appointmentStatus, String employeeName, Integer page) {
        PageRequest pageable = PageRequest.of(page, 4);
        Integer statusCode = (appointmentStatus == null) ? null : appointmentStatus.getCode();
        return appointmentRepository.findAllAppointmentsManagement(statusCode, employeeName, pageable);
    }
}

package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import org.springframework.data.domain.Page;

public interface GetAppointmentsManagementWithPaginationUseCasePort {
    Page<AppointmentsManagementResult> execute(AppointmentStatus appointmentStatus, String employeeName, Integer page);
}

package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import org.springframework.data.domain.Page;

public interface GetAppointmentsManagementWithPaginationAdapterPort {
    Page<AppointmentsManagementResult> get(AppointmentStatus appointmentStatus, String employeeName, Integer page);
}

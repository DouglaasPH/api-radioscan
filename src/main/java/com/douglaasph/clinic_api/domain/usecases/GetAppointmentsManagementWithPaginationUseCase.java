package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.ports.inbound.GetAppointmentsManagementWithPaginationUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentsManagementWithPaginationAdapterPort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class GetAppointmentsManagementWithPaginationUseCase implements GetAppointmentsManagementWithPaginationUseCasePort {
    private final GetAppointmentsManagementWithPaginationAdapterPort getAppointmentsManagementWithPaginationAdapterPort;

    public GetAppointmentsManagementWithPaginationUseCase(GetAppointmentsManagementWithPaginationAdapterPort getAppointmentsManagementWithPaginationAdapterPort) {
        this.getAppointmentsManagementWithPaginationAdapterPort = getAppointmentsManagementWithPaginationAdapterPort;
    }

    @Override
    public Page<AppointmentsManagementResult> execute(AppointmentStatus appointmentStatus, String employeeName, Integer page) {
        return getAppointmentsManagementWithPaginationAdapterPort.get(appointmentStatus, employeeName, page);
    }
}

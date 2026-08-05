package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentsManagementWithPaginationAdapterPort;
import com.douglaasph.clinic_api.domain.results.AppointmentsManagementResult;
import com.douglaasph.clinic_api.domain.usecases.GetAppointmentsManagementWithPaginationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAppointmentsManagementWithPaginationUseCaseTest {
    @Mock
    private GetAppointmentsManagementWithPaginationAdapterPort getAppointmentsManagementWithPaginationAdapterPort;

    @InjectMocks
    private GetAppointmentsManagementWithPaginationUseCase getAppointmentsManagementWithPaginationUseCase;

    @Test
    @DisplayName("Must return appointment page when credentials valid")
    void case1() {
        // arrange
        AppointmentsManagementResult appointment1 = new AppointmentsManagementResult();
        AppointmentsManagementResult appointment2 = new AppointmentsManagementResult();

        // Usa PageImpl encapsulando a lista de resultados
        Page<AppointmentsManagementResult> appointmentPage =
                new PageImpl<>(List.of(appointment1, appointment2));

        when(getAppointmentsManagementWithPaginationAdapterPort.get(null, null, null)).thenReturn(appointmentPage);

        // act
        Page<AppointmentsManagementResult> result = getAppointmentsManagementWithPaginationUseCase.execute(null, null, null);

        // assert (AssertJ)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.isEmpty()).isFalse();
    }
}

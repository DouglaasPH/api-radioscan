package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.ports.outbound.GetAllAvailabilitiesAppointmentsAdapterPort;
import com.douglaasph.clinic_api.domain.results.AvailabilitiesAppointmentsResult;
import com.douglaasph.clinic_api.domain.usecases.GetAllAvailabilitiesAppointmentsUseCase;
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
public class GetAllAvailabilitiesAppointmentsUseCaseTest {
    @Mock
    private GetAllAvailabilitiesAppointmentsAdapterPort getAllAvailabilitiesAppointmentsAdapterPort;

    @InjectMocks
    private GetAllAvailabilitiesAppointmentsUseCase getAllAvailabilitiesAppointmentsUseCase;

    @Test
    @DisplayName("Must return apoointment page when credentials are valid")
    void case1() {
        // arrange
        AvailabilitiesAppointmentsResult appointment1 = new AvailabilitiesAppointmentsResult();
        AvailabilitiesAppointmentsResult appointment2 = new AvailabilitiesAppointmentsResult();
        AvailabilitiesAppointmentsResult appointment3 = new AvailabilitiesAppointmentsResult();

        Page<AvailabilitiesAppointmentsResult> appointmentPage =
                new PageImpl<>(List.of(appointment1, appointment2, appointment3));

        when(getAllAvailabilitiesAppointmentsAdapterPort.get(null, null, null)).thenReturn(appointmentPage);

        // act
        Page<AvailabilitiesAppointmentsResult> result = getAllAvailabilitiesAppointmentsUseCase.execute(null, null, null);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.isEmpty()).isFalse();
    }
}

package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentsLinkedToThePatientAdapterPort;
import com.douglaasph.clinic_api.domain.results.AppointmentsLinkedToThePatientResult;
import com.douglaasph.clinic_api.domain.usecases.GetAppointmentLinkedToThePatientUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAppointmentLinkedToThePatientUseCaseTest {
    @Mock
    private GetAppointmentsLinkedToThePatientAdapterPort getAppointmentsLinkedToThePatientAdapterPort;

    @InjectMocks
    private GetAppointmentLinkedToThePatientUseCase getAppointmentLinkedToThePatientUseCase;

    private static final String EMAIL = "douglas@gmail.com";

    @Test
    @DisplayName("Must return appointment when credentials are valid")
    void case1() {
        // arrange
        AppointmentsLinkedToThePatientResult appointment1 =  new AppointmentsLinkedToThePatientResult();
        AppointmentsLinkedToThePatientResult appointment2 =  new AppointmentsLinkedToThePatientResult();
        when(getAppointmentsLinkedToThePatientAdapterPort.get(EMAIL)).thenReturn(List.of(appointment1, appointment2));

        // act
        List<AppointmentsLinkedToThePatientResult> result = getAppointmentLinkedToThePatientUseCase.execute(EMAIL);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.isEmpty()).isFalse();
    }
}

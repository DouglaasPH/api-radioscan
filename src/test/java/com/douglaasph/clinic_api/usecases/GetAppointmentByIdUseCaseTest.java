package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.GetAppointmentByIdUseCase;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetAppointmentByIdUseCaseTest {
    @Mock
    private GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;

    @InjectMocks
    private GetAppointmentByIdUseCase getAppointmentByIdUseCase;

    private static final Long APPOINTMENT_ID = 1L;

    @Test
    @DisplayName("Must return appointment when appointment id is valid")
    void case1() {
        // arrange
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, null, LocalDateTime.now().plusDays(2), AppointmentStatus.CANCELED, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        // at
        Appointment result = getAppointmentByIdUseCase.execute(APPOINTMENT_ID);

        // assert
        assertThat(result.getId()).isEqualTo(APPOINTMENT_ID);
    }

    @Test
    @DisplayName("Should propagate exception when appointment not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("Appointment", "id", APPOINTMENT_ID))
                .when(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);

        // act + assert
        assertThatThrownBy(() -> getAppointmentByIdUseCase.execute(APPOINTMENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Appointment not found with id: '" + APPOINTMENT_ID + "'");

    }
}

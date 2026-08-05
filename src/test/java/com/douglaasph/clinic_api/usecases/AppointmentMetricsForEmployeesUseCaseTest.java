package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.outbound.GetMetricsForEmployeeDashboardAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.results.EmployeesMetricsResult;
import com.douglaasph.clinic_api.domain.usecases.AppointmentMetricsForEmployeesUseCase;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentMetricsForEmployeesUseCaseTest {
    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @Mock
    private GetMetricsForEmployeeDashboardAdapterPort getMetricsForEmployeeDashboardAdapterPort;

    @InjectMocks
    private AppointmentMetricsForEmployeesUseCase appointmentMetricsForEmployeesUseCase;

    private static final String EMAIL = "douglas@radioscan.com";

    @Test
    @DisplayName("Must authenticate and return tokens when credentials are valid")
    void case1() {
        // arrange
        User user = new User(1L, null, EMAIL, null, null,null, null);
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        EmployeesMetricsResult resultMock = new EmployeesMetricsResult(1, 2, 3);
        when(getMetricsForEmployeeDashboardAdapterPort.get(user.getId())).thenReturn(resultMock);

        // act
        EmployeesMetricsResult result = appointmentMetricsForEmployeesUseCase.execute(EMAIL);

        // assert
        assertThat(result.getTotalAppointments()).isEqualTo(1);
        assertThat(result.getCompletedAppointments()).isEqualTo(2);
        assertThat(result.getPendingAppointments()).isEqualTo(3);

        verify(getUserByEmailAdapterPort).findByEmail(EMAIL);
        verify(getMetricsForEmployeeDashboardAdapterPort).get(user.getId());
    }

    @Test
    @DisplayName("Must authenticate and return tokens when user is not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("User", "email", EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(EMAIL);

        // act + assert
        assertThatThrownBy(() -> appointmentMetricsForEmployeesUseCase.execute(EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@radioscan.com'");

        verifyNoInteractions(getMetricsForEmployeeDashboardAdapterPort);
    }
}

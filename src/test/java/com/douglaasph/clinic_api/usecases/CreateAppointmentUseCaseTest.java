package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.ports.outbound.GetEmployeeByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.CreateAppointmentUseCase;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class CreateAppointmentUseCaseTest {
    @Mock
    private SaveAppointmentAdapterPort saveAppointmentAdapterPort;

    @Mock
    private GetEmployeeByIdAdapterPort getEmployeeByIdAdapterPort;

    @InjectMocks
    private CreateAppointmentUseCase createAppointmentUseCase;

    private static final Long EMPLOYEE_ID = 1L;
    private static final LocalDateTime DATE_HOUR = LocalDateTime.now().plusDays(7);

    @Test
    @DisplayName("Must return appointment when credentials are valid and employee is TECHNICAL")
    void case1() {
        // arrange
        Employee employee = new Employee(EMPLOYEE_ID, null, Position.TECHNICAL, null);
        when(getEmployeeByIdAdapterPort.get(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Appointment appointment = new Appointment(
                null,
                employee,
                null,
                DATE_HOUR,
                AppointmentStatus.AVAILABLE,
                AppointmentType.EXAM_CAPTURE,
                null
        );
        when(saveAppointmentAdapterPort.save(any(Appointment.class))).thenReturn(appointment);

        // act
        Appointment result = createAppointmentUseCase.execute(EMPLOYEE_ID, DATE_HOUR, AppointmentType.EXAM_CAPTURE);

        // assert
        assertThat(result.getEmployee()).isEqualTo(employee);
        assertThat(result.getDateHour()).isEqualTo(DATE_HOUR);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.AVAILABLE);
        assertThat(result.getType()).isEqualTo(AppointmentType.EXAM_CAPTURE);
    }

    @Test
    @DisplayName("Must return appointment when credentials are valid and employee is DOCTOR")
    void case2() {
        // arrange
        Employee employee = new Employee(EMPLOYEE_ID, null, Position.DOCTOR, null);
        when(getEmployeeByIdAdapterPort.get(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Appointment appointment = new Appointment(
                null,
                employee,
                null,
                DATE_HOUR,
                AppointmentStatus.AVAILABLE,
                AppointmentType.REPORT_REVIEW,
                null
        );
        when(saveAppointmentAdapterPort.save(any(Appointment.class))).thenReturn(appointment);

        // act
        Appointment result = createAppointmentUseCase.execute(EMPLOYEE_ID, DATE_HOUR, AppointmentType.REPORT_REVIEW);

        // assert
        assertThat(result.getEmployee()).isEqualTo(employee);
        assertThat(result.getDateHour()).isEqualTo(DATE_HOUR);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.AVAILABLE);
        assertThat(result.getType()).isEqualTo(AppointmentType.REPORT_REVIEW);
    }

    @Test
    @DisplayName("Should propagate exception when employee not found")
    void case3() {
        // arrange
        doThrow(new ResourceNotFoundException("Employee", "id", EMPLOYEE_ID))
                .when(getEmployeeByIdAdapterPort).get(EMPLOYEE_ID);

        // act + assert
        assertThatThrownBy(() -> createAppointmentUseCase.execute(EMPLOYEE_ID, DATE_HOUR, AppointmentType.EXAM_CAPTURE))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: '" + EMPLOYEE_ID + "'");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when Employee's is TECHNICAL position and Appointment type is REPORT_REVIEW")
    void case4() {
        // arrange
        Employee employee = new Employee(EMPLOYEE_ID, null, Position.TECHNICAL, null);
        when(getEmployeeByIdAdapterPort.get(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        // act + assert
        assertThatThrownBy(() -> createAppointmentUseCase.execute(EMPLOYEE_ID, DATE_HOUR, AppointmentType.REPORT_REVIEW))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Employee's position incompatible with the type of inquiry.");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when Employee's is DOCTOR position and Appointment type is EXAM_CAPTURE")
    void case5() {
        // arrange
        Employee employee = new Employee(EMPLOYEE_ID, null, Position.DOCTOR, null);
        when(getEmployeeByIdAdapterPort.get(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        // act + assert
        assertThatThrownBy(() -> createAppointmentUseCase.execute(EMPLOYEE_ID, DATE_HOUR, AppointmentType.EXAM_CAPTURE))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Employee's position incompatible with the type of inquiry.");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }
}

package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.CancelAppointmentUseCase;
import com.douglaasph.clinic_api.exceptions.AccessDeniedException;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class CancelAppointmentUseCaseTest {
    @Mock
    private GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;

    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @Mock
    private SaveAppointmentAdapterPort saveAppointmentAdapterPort;

    @InjectMocks
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    private static final Long APPOINTMENT_ID = 1L;
    private static final String EMAIL = "douglas@gmail.com";

    @Test
    @DisplayName("Must return appointment when credentials are valid and user is PATIENT for the appointment")
    void case1() {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, EMAIL, null, Roles.PATIENT, null, patient);
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, patient, LocalDateTime.now().plusDays(2), AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(saveAppointmentAdapterPort.save(appointment)).thenReturn(appointment);

        // act
        Appointment result = cancelAppointmentUseCase.execute(APPOINTMENT_ID, EMAIL);

        // assert
        assertThat(result.getId()).isEqualTo(APPOINTMENT_ID);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CANCELED);

        verify(getUserByEmailAdapterPort).findByEmail(EMAIL);
        verify(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);
        verify(saveAppointmentAdapterPort).save(appointment);
    }

    @Test
    @DisplayName("Must return appointment when credentials are valid and user is ADMIN")
    void case2() {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, EMAIL, null, Roles.ADMIN, null, patient);
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, patient, LocalDateTime.now().plusDays(2), AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(saveAppointmentAdapterPort.save(appointment)).thenReturn(appointment);

        // act
        Appointment result = cancelAppointmentUseCase.execute(APPOINTMENT_ID, EMAIL);

        // assert
        assertThat(result.getId()).isEqualTo(APPOINTMENT_ID);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CANCELED);

        verify(getUserByEmailAdapterPort).findByEmail(EMAIL);
        verify(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);
        verify(saveAppointmentAdapterPort).save(appointment);
    }

    @Test
    @DisplayName("Should propagate exception when appointment not found")
    void case4() {
        // arrange
        doThrow(new ResourceNotFoundException("Appointment", "id", APPOINTMENT_ID))
                .when(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);

        // act + assert
        assertThatThrownBy(() -> cancelAppointmentUseCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Appointment not found with id: '" + APPOINTMENT_ID + "'");

        verifyNoInteractions(getUserByEmailAdapterPort);
        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when appointment type is CANCELED")
    void case5() {
        // arrange
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, null, LocalDateTime.now().plusDays(2), AppointmentStatus.CANCELED, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        // act + assert
        assertThatThrownBy(() -> cancelAppointmentUseCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Appointment is already canceled.");

        verifyNoInteractions(getUserByEmailAdapterPort);
        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when user not found")
    void case6() {
        // arrange
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, null, LocalDateTime.now().plusDays(2), AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        doThrow(new ResourceNotFoundException("User", "email", EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(EMAIL);

        // act + assert
        assertThatThrownBy(() -> cancelAppointmentUseCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@gmail.com'");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when user is patient is not have permission to cancel")
    void case7() {
        // arrange
        Patient patientAnother = new Patient(6L, null, null, null);
        User userAnother = new User(3L, null, EMAIL, null, Roles.PATIENT, null, patientAnother);
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, patientAnother, LocalDateTime.now().plusDays(2), AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, EMAIL, null, Roles.PATIENT, null, patient);
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // act + assert
        assertThatThrownBy(() -> cancelAppointmentUseCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to cancel this appointment.");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when time is less than 24 hours")
    void case8() {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, EMAIL, null, Roles.PATIENT, null, patient);
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, patient, LocalDateTime.now().minusDays(2), AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // act + assert
        assertThatThrownBy(() -> cancelAppointmentUseCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("The appointment can only be cancelled with 24 hours' notice.");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }
}

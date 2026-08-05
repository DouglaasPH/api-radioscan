package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.BookExamCaptureUseCase;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
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
public class BookExamCaptureUseCaseTest {
    @Mock
    private SaveAppointmentAdapterPort saveAppointmentAdapterPort;

    @Mock
    private GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;

    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @InjectMocks
    private BookExamCaptureUseCase bookExamCaptureUseCase;

    private static final Long APPOINTMENT_ID = 1L;
    private static final String PATIENT_EMAIL = "douglas@gmail.com";

    @Test
    @DisplayName("Must return appointment when credentials are valid")
    void case1() throws AppointmentConflictException {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, PATIENT_EMAIL, null, null, null, patient);
        when(getUserByEmailAdapterPort.findByEmail(PATIENT_EMAIL)).thenReturn(Optional.of(user));
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, null, null, AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(saveAppointmentAdapterPort.save(appointment)).thenReturn(appointment);

        // act
        Appointment result = bookExamCaptureUseCase.execute(APPOINTMENT_ID, PATIENT_EMAIL);

        // asssert
        assertThat(result.getId()).isEqualTo(APPOINTMENT_ID);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(result.getPatient()).isEqualTo(patient);

        verify(getUserByEmailAdapterPort).findByEmail(PATIENT_EMAIL);
        verify(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);
        verify(saveAppointmentAdapterPort).save(appointment);
    }

    @Test
    @DisplayName("Should propagate exception when user not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("User", "email", PATIENT_EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(PATIENT_EMAIL);

        // act + assert
        assertThatThrownBy(() -> bookExamCaptureUseCase.execute(APPOINTMENT_ID, PATIENT_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@gmail.com'");

        verifyNoInteractions(getAppointmentByIdAdapterPort);
        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when patient is null")
    void case3() {
        // arrange
        User user = new User(2L, null, PATIENT_EMAIL, null, null, null, null);
        when(getUserByEmailAdapterPort.findByEmail(PATIENT_EMAIL)).thenReturn(Optional.of(user));

        // act + assert
        assertThatThrownBy(() -> bookExamCaptureUseCase.execute(APPOINTMENT_ID, PATIENT_EMAIL))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Authenticated user does not have an associated patient profile.");

        verifyNoInteractions(getAppointmentByIdAdapterPort);
        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when appointment not found")
    void case4() {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, PATIENT_EMAIL, null, null, null, patient);
        when(getUserByEmailAdapterPort.findByEmail(PATIENT_EMAIL)).thenReturn(Optional.of(user));
        doThrow(new ResourceNotFoundException("Appointment", "id", APPOINTMENT_ID))
                .when(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);

        // act + assert
        assertThatThrownBy(() -> bookExamCaptureUseCase.execute(APPOINTMENT_ID, PATIENT_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Appointment not found with id: '" + APPOINTMENT_ID + "'");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when appointment type is REPORT_REVIEW")
    void case5() {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, PATIENT_EMAIL, null, null, null, patient);
        when(getUserByEmailAdapterPort.findByEmail(PATIENT_EMAIL)).thenReturn(Optional.of(user));
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, patient, null, AppointmentStatus.AVAILABLE, AppointmentType.REPORT_REVIEW, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        // act + assert
        assertThatThrownBy(() -> bookExamCaptureUseCase.execute(APPOINTMENT_ID, PATIENT_EMAIL))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessage("This appointment is not of the EXAM_CAPTURE type.");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when appointment is already reserved by another patient")
    void case6() {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, PATIENT_EMAIL, null, null, null, patient);
        when(getUserByEmailAdapterPort.findByEmail(PATIENT_EMAIL)).thenReturn(Optional.of(user));
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, patient, null, AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        // act + assert
        assertThatThrownBy(() -> bookExamCaptureUseCase.execute(APPOINTMENT_ID, PATIENT_EMAIL))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessage("This time slot is already reserved by another patient.");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when appointent status is different from available")
    void case7() {
        // arrange
        Patient patient = new Patient(5L, null, null, null);
        User user = new User(2L, null, PATIENT_EMAIL, null, null, null, patient);
        when(getUserByEmailAdapterPort.findByEmail(PATIENT_EMAIL)).thenReturn(Optional.of(user));
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, null, null, AppointmentStatus.CANCELED, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        // act + assert
        assertThatThrownBy(() -> bookExamCaptureUseCase.execute(APPOINTMENT_ID, PATIENT_EMAIL))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessage("This time slot is not available for scheduling.");

        verifyNoInteractions(saveAppointmentAdapterPort);
    }
}

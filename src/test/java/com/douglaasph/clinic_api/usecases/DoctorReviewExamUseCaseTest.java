package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.outbound.GetAppointmentByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetXRayReportByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveAppointmentAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveXRayReportAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.DoctorReviewExamUseCase;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class DoctorReviewExamUseCaseTest {
    @Mock
    private GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;

    @Mock
    private GetXRayReportByIdAdapterPort getXRayReportByIdAdapterPort;

    @Mock
    private SaveAppointmentAdapterPort saveAppointmentAdapterPort;

    @Mock
    private SaveXRayReportAdapterPort saveXRayReportAdapterPort;

    @InjectMocks
    private DoctorReviewExamUseCase doctorReviewExamUseCase;

    private static final Long APPOINTMENT_ID = 1L;
    private static final String FINAL_DOCTOR_DIAGNOSIS = "final doctor diagnosis";

    @Test
    @DisplayName("Must return XRayReport when credentials are valid")
    void case1() {
        // arrange
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, null, LocalDateTime.now().plusDays(2), AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        XRayReport xRayReport = new XRayReport(
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                List.of(appointment)
        );
        when(getXRayReportByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(xRayReport));
        when(saveAppointmentAdapterPort.save(appointment)).thenReturn(appointment);
        when(saveXRayReportAdapterPort.save(xRayReport)).thenReturn(xRayReport);

        // act
        XRayReport result = doctorReviewExamUseCase.execute(APPOINTMENT_ID, FINAL_DOCTOR_DIAGNOSIS);

        // assert
        assertThat(result.getFinalMedicalDiagnosis()).isEqualTo(FINAL_DOCTOR_DIAGNOSIS);
        assertThat(result.getProcessingStatus()).isEqualTo(4);
        assertThat(result.isReleasedToPatient()).isTrue();

        verify(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);
        verify(getXRayReportByIdAdapterPort).get(APPOINTMENT_ID);
        verify(saveAppointmentAdapterPort).save(appointment);
        verify(saveXRayReportAdapterPort).save(xRayReport);
    }

    @Test
    @DisplayName("Should propagate exception when appointment not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("Appointment", "id", APPOINTMENT_ID))
                .when(getAppointmentByIdAdapterPort).get(APPOINTMENT_ID);

        // act + assert
        assertThatThrownBy(() -> doctorReviewExamUseCase.execute(APPOINTMENT_ID, FINAL_DOCTOR_DIAGNOSIS))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Appointment not found with id: '" + APPOINTMENT_ID + "'");

        verifyNoInteractions(getXRayReportByIdAdapterPort);
        verifyNoInteractions(saveAppointmentAdapterPort);
        verifyNoInteractions(saveXRayReportAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when xRayReport not found")
    void case3() {
        // arrange
        Appointment appointment = new Appointment(APPOINTMENT_ID, null, null, LocalDateTime.now().plusDays(2), AppointmentStatus.AVAILABLE, AppointmentType.EXAM_CAPTURE, null);
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        doThrow(new ResourceNotFoundException("XRayReport", "id of the appointment", APPOINTMENT_ID))
                .when(getXRayReportByIdAdapterPort).get(APPOINTMENT_ID);

        // act + assert
        assertThatThrownBy(() -> doctorReviewExamUseCase.execute(APPOINTMENT_ID, FINAL_DOCTOR_DIAGNOSIS))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("XRayReport not found with id of the appointment: '" + APPOINTMENT_ID + "'");

        verifyNoInteractions(saveAppointmentAdapterPort);
        verifyNoInteractions(saveXRayReportAdapterPort);
    }
}

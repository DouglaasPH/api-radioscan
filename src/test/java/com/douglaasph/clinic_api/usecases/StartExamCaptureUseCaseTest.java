package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.outbound.*;
import com.douglaasph.clinic_api.domain.results.UrlPressignedS3Result;
import com.douglaasph.clinic_api.domain.usecases.StartExamCaptureUseCase;
import com.douglaasph.clinic_api.exceptions.AccessDeniedException;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartExamCaptureUseCaseTest {
    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    @Mock
    private GetAppointmentByIdAdapterPort getAppointmentByIdAdapterPort;
    @Mock
    private SaveAppointmentAdapterPort saveAppointmentAdapterPort;
    @Mock
    private SaveXRayReportAdapterPort saveXRayReportAdapterPort;
    @Mock
    private FileStorageAdapterPort fileStorageAdapterPort;

    private StartExamCaptureUseCase useCase;

    private static final Long APPOINTMENT_ID = 10L;
    private static final Long EMPLOYEE_ID = 1L;
    private static final String EMAIL = "douglas@gmail.com";

    @BeforeEach
    void setUp() {
        useCase = new StartExamCaptureUseCase(
                getUserByEmailAdapterPort,
                getAppointmentByIdAdapterPort,
                saveAppointmentAdapterPort,
                saveXRayReportAdapterPort,
                fileStorageAdapterPort
        );
    }

    private User userWithEmployee(Long employeeId) {
        Employee employee = new Employee();
        employee.setId(employeeId);

        User user = new User();
        user.setEmployee(employee);
        return user;
    }

    private User userWithoutEmployee() {
        User user = new User();
        user.setEmployee(null);
        return user;
    }

    private Appointment appointmentAssignedTo(Long employeeId, AppointmentType type, AppointmentStatus status) {
        Employee employee = new Employee();
        employee.setId(employeeId);

        Appointment appointment = new Appointment();
        appointment.setId(APPOINTMENT_ID);
        appointment.setEmployee(employee);
        appointment.setType(type);
        appointment.setStatus(status);
        return appointment;
    }

    @Test
    @DisplayName("Must start exam capture and generate upload URL when everything is valid.")
    void case1() throws AppointmentConflictException {
        User user = userWithEmployee(EMPLOYEE_ID);
        Appointment appointment = appointmentAssignedTo(EMPLOYEE_ID, AppointmentType.EXAM_CAPTURE, AppointmentStatus.SCHEDULED);

        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(saveXRayReportAdapterPort.save(any(XRayReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // devolve o mesmo objeto recebido
        when(fileStorageAdapterPort.generateUploadUrl(anyString())).thenReturn("https://s3.fake/presigned-url");

        UrlPressignedS3Result result = useCase.execute(APPOINTMENT_ID, EMAIL);

        assertThat(result.getUrlPreassignedS3()).isEqualTo("https://s3.fake/presigned-url");
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(appointment.getxRayReport()).isNotNull();

        verify(saveAppointmentAdapterPort, times(2)).save(appointment);
        verify(saveXRayReportAdapterPort).save(any(XRayReport.class));
        verify(fileStorageAdapterPort).generateUploadUrl(anyString());
    }

    @Test
    @DisplayName("Must generate an S3 key with the prefix 'Exams' and the extension '.png'.")
    void case2() throws AppointmentConflictException {
        User user = userWithEmployee(EMPLOYEE_ID);
        Appointment appointment = appointmentAssignedTo(EMPLOYEE_ID, AppointmentType.EXAM_CAPTURE, AppointmentStatus.SCHEDULED);

        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(saveXRayReportAdapterPort.save(any(XRayReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(fileStorageAdapterPort.generateUploadUrl(anyString())).thenReturn("https://s3.fake/presigned-url");

        useCase.execute(APPOINTMENT_ID, EMAIL);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(fileStorageAdapterPort).generateUploadUrl(keyCaptor.capture());

        assertThat(keyCaptor.getValue())
                .startsWith("exams/")
                .endsWith(".png");
    }

    @Test
    @DisplayName("Should throw Resource Not Found when user does not exist")
    void case3() {
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(getAppointmentByIdAdapterPort, saveAppointmentAdapterPort, saveXRayReportAdapterPort, fileStorageAdapterPort);
    }

    @Test
    @DisplayName("Should throw AccessDenied when the logged-in user is not an employee.")
    void case4() {
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(userWithoutEmployee()));

        assertThatThrownBy(() -> useCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not registered as an employee");

        verifyNoInteractions(getAppointmentByIdAdapterPort, saveAppointmentAdapterPort, saveXRayReportAdapterPort, fileStorageAdapterPort);
    }

    @Test
    @DisplayName("Should throw ResourceNotFound when the appointment does not exist.")
    void case5() {
        User user = userWithEmployee(EMPLOYEE_ID);
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(saveAppointmentAdapterPort, saveXRayReportAdapterPort, fileStorageAdapterPort);
    }

    @Test
    @DisplayName("Should throw AccessDenied when the appointment has no assigned employee.")
    void case6() {
        User user = userWithEmployee(EMPLOYEE_ID);
        Appointment appointment = appointmentAssignedTo(EMPLOYEE_ID, AppointmentType.EXAM_CAPTURE, AppointmentStatus.SCHEDULED);
        appointment.setEmployee(null);

        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> useCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not assigned to you");
    }

    @Test
    @DisplayName("Should throw AccessDenied when the appointment is assigned to another employee.")
    void case7() {
        User user = userWithEmployee(EMPLOYEE_ID);
        Appointment appointment = appointmentAssignedTo(999L, AppointmentType.EXAM_CAPTURE, AppointmentStatus.SCHEDULED); // outro funcionário

        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> useCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not assigned to you");
    }

    @Test
    @DisplayName("Must trigger BusinessRule when AppointmentType is not ExamCapture.")
    void case8() {
        User user = userWithEmployee(EMPLOYEE_ID);
        Appointment appointment = appointmentAssignedTo(EMPLOYEE_ID, AppointmentType.REPORT_REVIEW, AppointmentStatus.SCHEDULED);

        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> useCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not intended for the collection of test samples");

        verifyNoInteractions(saveAppointmentAdapterPort, saveXRayReportAdapterPort, fileStorageAdapterPort);
    }

    @Test
    @DisplayName("Must throw AppointmentConflict when AppointmentStatus is not SCHEDULED.")
    void case9() {
        User user = userWithEmployee(EMPLOYEE_ID);
        Appointment appointment = appointmentAssignedTo(EMPLOYEE_ID, AppointmentType.EXAM_CAPTURE, AppointmentStatus.COMPLETED);

        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(getAppointmentByIdAdapterPort.get(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> useCase.execute(APPOINTMENT_ID, EMAIL))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("must have the status SCHEDULED");

        verifyNoInteractions(saveAppointmentAdapterPort, saveXRayReportAdapterPort, fileStorageAdapterPort);
    }
}

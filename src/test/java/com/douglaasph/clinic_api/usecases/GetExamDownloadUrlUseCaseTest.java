package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.outbound.FileStorageAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetXRayReportByIdAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.GetExamDownloadUrlUseCase;
import com.douglaasph.clinic_api.exceptions.ReportNotReleasedException;
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
public class GetExamDownloadUrlUseCaseTest {
    @Mock
    private GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    @Mock
    private GetXRayReportByIdAdapterPort getXRayReportByIdAdapterPort;

    @Mock
    private FileStorageAdapterPort fileStorageAdapterPort;

    @InjectMocks
    private GetExamDownloadUrlUseCase getExamDownloadUrlUseCase;

    private static final Long REPORT_ID = 1L;
    private static final String EMAIL = "douglas@gmail.com";

    @Test
    @DisplayName("Must return exam download url when credentials are valid")
    void case1() {
        // arrange
        User user = new User(
                null,
                null,
                EMAIL,
                null,
                Roles.PATIENT,
                null,
                null
        );
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        XRayReport xRayReport = new XRayReport(
                null,
                null,
                "s3Key",
                null,
                null,
                null,
                true,
                null
        );
        when(getXRayReportByIdAdapterPort.get(REPORT_ID)).thenReturn(Optional.of(xRayReport));
        when(fileStorageAdapterPort.generateDownloadUrl(xRayReport.getS3Key())).thenReturn(xRayReport.getS3Key());

        // act
        String result = getExamDownloadUrlUseCase.execute(REPORT_ID, EMAIL);

        // assert
        assertThat(result).isEqualTo("s3Key");
    }

    @Test
    @DisplayName("Should propagate exception when user not found")
    void case2() {
        // arrange
        doThrow(new ResourceNotFoundException("User", "email", EMAIL))
                .when(getUserByEmailAdapterPort).findByEmail(EMAIL);

        // act + assert
        assertThatThrownBy(() -> getExamDownloadUrlUseCase.execute(REPORT_ID, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: 'douglas@gmail.com'");

        verifyNoInteractions(getXRayReportByIdAdapterPort);
        verifyNoInteractions(fileStorageAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when xRayReport not found")
    void case3() {
        // arrange
        // arrange
        User user = new User(
                null,
                null,
                EMAIL,
                null,
                Roles.PATIENT,
                null,
                null
        );
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        doThrow(new ResourceNotFoundException("Report", "id", REPORT_ID))
                .when(getXRayReportByIdAdapterPort).get(REPORT_ID);

        // act + assert
        assertThatThrownBy(() -> getExamDownloadUrlUseCase.execute(REPORT_ID, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Report not found with id: '" + REPORT_ID + "'");

        verifyNoInteractions(fileStorageAdapterPort);
    }

    @Test
    @DisplayName("Should propagate exception when exam result has not been released by the doctor yet")
    void case4() {
        // arrange
        User user = new User(
                null,
                null,
                EMAIL,
                null,
                Roles.PATIENT,
                null,
                null
        );
        when(getUserByEmailAdapterPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        XRayReport xRayReport = new XRayReport(
                null,
                null,
                "s3Key",
                null,
                null,
                null,
                false,
                null
        );
        when(getXRayReportByIdAdapterPort.get(REPORT_ID)).thenReturn(Optional.of(xRayReport));

        // act + assert
        assertThatThrownBy(() -> getExamDownloadUrlUseCase.execute(REPORT_ID, EMAIL))
                .isInstanceOf(ReportNotReleasedException.class)
                .hasMessage("This exam result has not been released by the doctor yet.");

        verifyNoInteractions(fileStorageAdapterPort);
    }
}

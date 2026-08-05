package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.ports.outbound.GetDashboardMetricsForAdminAdapterPort;
import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;
import com.douglaasph.clinic_api.domain.usecases.GetDashboardMetricsForAdminUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetDashboardMetricsForAdminUseCaseTest {
   @Mock
   private GetDashboardMetricsForAdminAdapterPort getDashboardMetricsForAdminAdapterPort;

   @InjectMocks
    private GetDashboardMetricsForAdminUseCase getDashboardMetricsForAdminUseCase;

   @Test
   @DisplayName("Must return dashboard metrics page")
    void case1() {
       // arrange
       DashboardAdminMetricsResult dashboardAdminMetricsResult = new DashboardAdminMetricsResult(1, 2, 3);
       when(getDashboardMetricsForAdminAdapterPort.get()).thenReturn(dashboardAdminMetricsResult);

       // act
       DashboardAdminMetricsResult result = getDashboardMetricsForAdminUseCase.execute();

       // assert
       assertThat(result.getNumberOfExamsPerformed()).isEqualTo(1);
       assertThat(result.getAvailableAppointmentSlots()).isEqualTo(2);
       assertThat(result.getTotalNumberOfPatients()).isEqualTo(3);
   }
}

package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.ports.outbound.GetMetricsEmployeesForAdminAdapterPort;
import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;
import com.douglaasph.clinic_api.domain.usecases.GetMetricsEmployeesForAdminUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetMetricsEmployeesForAdminUseCaseTest {
    @Mock
    private GetMetricsEmployeesForAdminAdapterPort getMetricsEmployeesForAdminAdapterPort;

    @InjectMocks
    private GetMetricsEmployeesForAdminUseCase getMetricsEmployeesForAdminUseCase;

    @Test
    @DisplayName("Must return metrics")
    void case1() {
        // arrange
        MetricsEmployeesForAdminResult metricsEmployeesForAdminResult = new MetricsEmployeesForAdminResult(1, 2, 3);
        when(getMetricsEmployeesForAdminAdapterPort.get()).thenReturn(metricsEmployeesForAdminResult);

        // act
        MetricsEmployeesForAdminResult result = getMetricsEmployeesForAdminUseCase.execute();

        // assert
        assertThat(result.getNumberOfEmplyees()).isEqualTo(1);
        assertThat(result.getNumberOfDoctors()).isEqualTo(2);
        assertThat(result.getNumberOfTechnicians()).isEqualTo(3);
    }
}

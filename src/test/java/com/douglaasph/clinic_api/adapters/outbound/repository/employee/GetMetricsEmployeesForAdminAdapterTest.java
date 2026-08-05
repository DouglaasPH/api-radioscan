package com.douglaasph.clinic_api.adapters.outbound.repository.employee;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.GetMetricsEmployeesForAdminAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
public class GetMetricsEmployeesForAdminAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private GetMetricsEmployeesForAdminAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GetMetricsEmployeesForAdminAdapter(employeeRepository);
    }

    @Test
    @DisplayName("Should return correct employee metrics count")
    void shouldReturnCorrectMetrics() {
        // arrange
        UserJpaEntity user1 = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Doctor One", "doc1@example.com", "pass123", Roles.EMPLOYEE)
        );
        UserJpaEntity user2 = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Doctor Two", "doc2@example.com", "pass123", Roles.EMPLOYEE)
        );
        UserJpaEntity user3 = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Tech One", "tech1@example.com", "pass123", Roles.EMPLOYEE)
        );

        // arrange
        entityManager.persistAndFlush(new EmployeeJpaEntity(null, "43345464", Position.DOCTOR, user1));
        entityManager.persistAndFlush(new EmployeeJpaEntity(null, "44355464", Position.DOCTOR, user2));
        entityManager.persistAndFlush(new EmployeeJpaEntity(null, "43456464", Position.TECHNICAL, user3));

        // act
        MetricsEmployeesForAdminResult result = adapter.get();

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfEmplyees()).isEqualTo(3);
        assertThat(result.getNumberOfDoctors()).isEqualTo(2);
        assertThat(result.getNumberOfTechnicians()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle empty lists gracefully")
    void shouldHandleEmptyLists() {
        // act
        MetricsEmployeesForAdminResult result = adapter.get();

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfEmplyees()).isEqualTo(0);
        assertThat(result.getNumberOfDoctors()).isEqualTo(0);
        assertThat(result.getNumberOfTechnicians()).isEqualTo(0);
    }
}

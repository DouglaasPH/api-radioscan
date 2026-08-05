package com.douglaasph.clinic_api.adapters.outbound.repository.employee;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.GetEmployeeByIdAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetEmployeeByIdAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private GetEmployeeByIdAdapter getEmployeeByIdAdapter;

    @BeforeEach
    void setUp() {
        getEmployeeByIdAdapter = new GetEmployeeByIdAdapter(employeeRepository);
    }

    @Test
    @DisplayName("Must find employee by id")
    void case1() {
        // arrange
        UserJpaEntity userJpaEntity = new UserJpaEntity(
                null,
                "Example Example",
                "example@example.com",
                "example",
                Roles.PATIENT
        );
        entityManager.persistAndFlush(userJpaEntity);
        EmployeeJpaEntity employeeJpaEntity = new EmployeeJpaEntity(
                null,
                "3049343",
                Position.TECHNICAL,
                userJpaEntity
        );
        EmployeeJpaEntity savedPatient = entityManager.persistAndFlush(employeeJpaEntity);

        // act
        Optional<Employee> result = getEmployeeByIdAdapter.get(savedPatient.getId());

        // assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedPatient.getId());
        assertThat(result.get().getLicenseNumber()).isEqualTo("3049343");
        assertThat(result.get().getPosition()).isEqualTo(Position.TECHNICAL);
    }

    @Test
    @DisplayName("Should return an empty Optional when the id does not exist")
    void case2() {
        // act
        Optional<Employee> result = getEmployeeByIdAdapter.get(1L);

        // assert
        assertThat(result).isEmpty();
    }
}

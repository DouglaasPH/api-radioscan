package com.douglaasph.clinic_api.adapters.outbound.repository.employee;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.SaveEmployeeAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaMapper;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class SaveEmployeeAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private SaveEmployeeAdapter saveEmployeeAdapter;

    @BeforeEach
    void setUp() { saveEmployeeAdapter = new SaveEmployeeAdapter(employeeRepository); }

    @Test
    @DisplayName("Must save employee")
    void case1() {
        // arrange
        UserJpaEntity userEntity = new UserJpaEntity(
                null,
                "Patient User",
                "patient@example.com",
                "password123",
                Roles.EMPLOYEE
        );
        UserJpaEntity savedUser = entityManager.persistAndFlush(userEntity);
        Employee employee = new Employee(
                null,
                "4540954",
                Position.TECHNICAL,
                UserJpaMapper.toDomain(savedUser)
        );

        // act
        Employee result = saveEmployeeAdapter.save(employee);

        // assert
        assertThat(result.getLicenseNumber()).isEqualTo("4540954");
        assertThat(result.getPosition()).isEqualTo(Position.TECHNICAL);
    }

    @Test
    @DisplayName("Should propagate exception when lincense number already exists")
    void case2() {
        // arrange
        UserJpaEntity userEntityFirst = new UserJpaEntity(
                null,
                "Patient User",
                "patient@example.com",
                "password123",
                Roles.EMPLOYEE
        );
        UserJpaEntity savedUserFirst = entityManager.persistAndFlush(userEntityFirst);
        EmployeeJpaEntity employeeFist = new EmployeeJpaEntity(
                null,
                "00000",
                Position.TECHNICAL,
                savedUserFirst
        );
        entityManager.persistAndFlush(employeeFist);

        UserJpaEntity userEntityDuplicate = new UserJpaEntity(
                null,
                "Patient User",
                "patient2@example.com",
                "password123",
                Roles.PATIENT
        );
        entityManager.persistAndFlush(userEntityDuplicate);
        Employee employeeDuplicate = new Employee(
                null,
                "00000",
                Position.DOCTOR,
                UserJpaMapper.toDomain(userEntityDuplicate)
        );

        // act + assert
        assertThatThrownBy(() -> saveEmployeeAdapter.save(employeeDuplicate))
                .isInstanceOf(DatabaseException.class)
                .hasMessage("Registration error: license number already exist.");
    }
}

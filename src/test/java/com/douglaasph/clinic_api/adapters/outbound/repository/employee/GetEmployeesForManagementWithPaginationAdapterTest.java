package com.douglaasph.clinic_api.adapters.outbound.repository.employee;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.GetEmployeesForManagementWithPaginationAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserRepository;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetEmployeesForManagementWithPaginationAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private GetEmployeesForManagementWithPaginationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GetEmployeesForManagementWithPaginationAdapter(userRepository);
    }

    @Test
    @DisplayName("Should return paginated list of employees applying filters correctly")
    void shouldReturnPaginatedEmployeesWithFilters() {
        // arrange
        UserJpaEntity user = new UserJpaEntity(
                null,
                "Example example",
                "example@example.com",
                "password123",
                Roles.EMPLOYEE
        );
        UserJpaEntity savedUser = entityManager.persistAndFlush(user);

        EmployeeJpaEntity employee = new EmployeeJpaEntity(
                null,
                "3455",
                Position.DOCTOR,
                savedUser
        );
        entityManager.persistAndFlush(employee);

        // act
        Page<User> result = adapter.get("Example example", Position.DOCTOR, 0);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Example example");
        assertThat(result.getPageable().getPageSize()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should return all employees when filters are null")
    void shouldReturnEmployeesWhenFiltersAreNull() {
        // arrange
        UserJpaEntity user = new UserJpaEntity(
                null,
                "Example example",
                "example@example.com",
                "password123",
                Roles.EMPLOYEE
        );
        UserJpaEntity savedUser = entityManager.persistAndFlush(user);

        EmployeeJpaEntity employee = new EmployeeJpaEntity(
                null,
                "435566565",
                Position.TECHNICAL,
                savedUser
        );
        entityManager.persistAndFlush(employee);

        // act
        Page<User> result = adapter.get(null, null, 0);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }
}

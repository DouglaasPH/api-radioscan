package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.ports.outbound.GetEmployeesForManagementWithPaginationAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.GetEmployeesForManagementWithPaginationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetEmployeesForManagementWithPaginationUseCaseTest {
    @Mock
    private GetEmployeesForManagementWithPaginationAdapterPort getEmployeesForManagementWithPaginationAdapterPort;

    @InjectMocks
    private GetEmployeesForManagementWithPaginationUseCase getEmployeesForManagementWithPaginationUseCase;

    @Test
    @DisplayName("Must return employees for management with pagination")
    void case1() {
        User user1 = new User();
        User user2 = new User();

        Page<User> page =
                new PageImpl<>(List.of(user1, user2));

        when(getEmployeesForManagementWithPaginationAdapterPort.get("name", Position.TECHNICAL, 1)).thenReturn(page);

        // act
        Page<User> result = getEmployeesForManagementWithPaginationUseCase.execute("name", Position.TECHNICAL, 1);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.isEmpty()).isFalse();
    }
}

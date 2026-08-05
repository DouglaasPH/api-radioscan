package com.douglaasph.clinic_api.usecases;

import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.outbound.PasswordEncoderAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveEmployeeAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveUserAdapterPort;
import com.douglaasph.clinic_api.domain.usecases.CreateEmployeeAccountUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateEmployeeAccountUseCaseTest {
    @Mock
    private SaveUserAdapterPort saveUserAdapterPort;

    @Mock
    private SaveEmployeeAdapterPort saveEmployeeAdapterPort;

    @Mock
    private PasswordEncoderAdapterPort passwordEncoderAdapterPort;

    @InjectMocks
    private CreateEmployeeAccountUseCase createEmployeeAccountUseCase;

    private static final String NAME = "Douglas Phelipe";
    private static final String EMAIL = "douglas@gmail.com";
    private static final String PASSWORD = "password";
    private static final String LICENSE_NUMBER = "licensenumber";

    @Test
    @DisplayName("Must return employee when credentials are valid")
    void case1() {
        // arrange
        User user = new User(
                null,
                NAME,
                EMAIL,
                PASSWORD,
                Roles.EMPLOYEE,
                null,
                null
        );
        when(passwordEncoderAdapterPort.encode(PASSWORD)).thenReturn(PASSWORD);
        when(saveUserAdapterPort.save(any(User.class))).thenReturn(user);
        Employee employee = new Employee(
                null,
                LICENSE_NUMBER,
                Position.TECHNICAL,
                user
        );
        when(saveEmployeeAdapterPort.save(any(Employee.class))).thenReturn(employee);

        // act
        Employee result = createEmployeeAccountUseCase.execute(NAME, EMAIL, PASSWORD, LICENSE_NUMBER, Position.TECHNICAL);

        // assert
        assertThat(result.getPosition()).isEqualTo(Position.TECHNICAL);
        assertThat(result.getLicenseNumber()).isEqualTo(LICENSE_NUMBER);
        assertThat(result.getUser().getName()).isEqualTo(NAME);
        assertThat(result.getUser().getEmail()).isEqualTo(EMAIL);
        assertThat(result.getUser().getPassword()).isEqualTo(PASSWORD);
        assertThat(result.getUser().getRole()).isEqualTo(Roles.EMPLOYEE);
    }
}

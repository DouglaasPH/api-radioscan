package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.inbound.CreateEmployeeAccountUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.PasswordEncoderAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveEmployeeAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveUserAdapterPort;
import org.springframework.stereotype.Service;

@Service
public class CreateEmployeeAccountUseCase implements CreateEmployeeAccountUseCasePort {
    private final SaveUserAdapterPort saveUserAdapterPort;
    private final SaveEmployeeAdapterPort saveEmployeeAdapterPort;
    private final PasswordEncoderAdapterPort passwordEncoderAdapterPort;

    public CreateEmployeeAccountUseCase(SaveUserAdapterPort saveUserAdapterPort, SaveEmployeeAdapterPort saveEmployeeAdapterPort, PasswordEncoderAdapterPort passwordEncoderAdapterPort) {
        this.saveUserAdapterPort = saveUserAdapterPort;
        this.saveEmployeeAdapterPort = saveEmployeeAdapterPort;
        this.passwordEncoderAdapterPort = passwordEncoderAdapterPort;
    }

    @Override
    public Employee execute(String name, String email, String password, String licenseNumber, Position position) {
        User user = new User(
                null,
                name,
                email,
                passwordEncoderAdapterPort.encode(password),
                Roles.EMPLOYEE,
                null,
                null
        );
        User savedUser = saveUserAdapterPort.save(user);

        Employee employee = new Employee(
                null,
                licenseNumber,
                position,
                savedUser
        );

        return saveEmployeeAdapterPort.save(employee);
    }
}

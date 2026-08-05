package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.inbound.UpdateUserDataUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveUserAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserDataUseCase implements UpdateUserDataUseCasePort {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final SaveUserAdapterPort saveUserAdapterPort;

    public UpdateUserDataUseCase(GetUserByEmailAdapterPort getUserByEmailAdapterPort, SaveUserAdapterPort saveUserAdapterPort) {
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.saveUserAdapterPort = saveUserAdapterPort;
    }

    @Override
    public User execute(String cpf, String phone, String currentEmail) {
        User user = this.getUserByEmailAdapterPort.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentEmail));

        if (user.getRole() == Roles.PATIENT) {
            if (cpf != null) user.getPatient().setCpf(cpf);
            if (phone != null) user.getPatient().setPhone(phone);
        }

        return this.saveUserAdapterPort.save(user);
    }

}

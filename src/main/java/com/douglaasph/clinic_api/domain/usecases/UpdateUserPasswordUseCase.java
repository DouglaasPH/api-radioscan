package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.inbound.UpdateUserPasswordUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.PasswordEncoderAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.SaveUserAdapterPort;
import com.douglaasph.clinic_api.exceptions.InvalidPasswordException;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserPasswordUseCase implements UpdateUserPasswordUseCasePort {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final SaveUserAdapterPort saveUserAdapterPort;
    private final PasswordEncoderAdapterPort passwordEncoderAdapterPort;

    public UpdateUserPasswordUseCase(GetUserByEmailAdapterPort getUserByEmailAdapterPort, SaveUserAdapterPort saveUserAdapterPort, PasswordEncoderAdapterPort passwordEncoderAdapterPort) {
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.saveUserAdapterPort = saveUserAdapterPort;
        this.passwordEncoderAdapterPort = passwordEncoderAdapterPort;
    }

    @Override
    public User execute(String email, String currentPassword, String newPassword) {
        User user = this.getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (!passwordEncoderAdapterPort.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("The current password is incorrect.");
        }

        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("The new password must be different from the current password.");
        }

        user.setPassword(passwordEncoderAdapterPort.encode(newPassword));

        return this.saveUserAdapterPort.save(user);
    }
}

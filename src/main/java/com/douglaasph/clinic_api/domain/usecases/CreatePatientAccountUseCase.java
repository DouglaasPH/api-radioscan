package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.ports.inbound.CreatePatientAccountUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.*;
import org.springframework.stereotype.Service;

@Service
public class CreatePatientAccountUseCase implements CreatePatientAccountUseCasePort {
    private final SaveUserAdapterPort saveUserAdapterPort;
    private final SavePatientAdapterPort savePatientAdapterPort;
    private final PasswordEncoderAdapterPort passwordEncoderAdapterPort;
    private final TokenProviderAdapterPort tokenProviderAdapterPort;
    private final CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort;

    public CreatePatientAccountUseCase(SaveUserAdapterPort saveUserAdapterPort, SavePatientAdapterPort savePatientAdapterPort, PasswordEncoderAdapterPort passwordEncoderAdapterPort, TokenProviderAdapterPort tokenProviderAdapterPort, CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort) {
        this.saveUserAdapterPort = saveUserAdapterPort;
        this.savePatientAdapterPort = savePatientAdapterPort;
        this.passwordEncoderAdapterPort = passwordEncoderAdapterPort;
        this.tokenProviderAdapterPort = tokenProviderAdapterPort;
        this.createRefreshTokenAdapterPort = createRefreshTokenAdapterPort;
    }

    @Override
    public LoginResult execute(String name, String email, String password, String cpf, String phone) {
        User user = new User(
                null,
                name,
                email,
                passwordEncoderAdapterPort.encode(password),
                Roles.PATIENT,
                null,
                null
        );
        User savedUser = saveUserAdapterPort.save(user);

        Patient patient = new Patient(
                null,
                cpf,
                phone,
                savedUser
        );
        savePatientAdapterPort.save(patient);

        String accessToken = tokenProviderAdapterPort.generateToken(email);
        String refreshToken = createRefreshTokenAdapterPort.create(email).getToken();

        return new LoginResult(accessToken, refreshToken);
    }
}

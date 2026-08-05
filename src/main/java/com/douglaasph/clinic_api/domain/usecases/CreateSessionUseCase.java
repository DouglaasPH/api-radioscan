package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.RefreshToken;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateRefreshTokenAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.CreateSessionUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.DeleteRefreshTokensByUserIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CreateSessionUseCase implements CreateSessionUseCasePort {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final DeleteRefreshTokensByUserIdAdapterPort deleteRefreshTokensByUserIdAdapterPort;
    private final CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort;

    public CreateSessionUseCase(GetUserByEmailAdapterPort getUserByEmailAdapterPort,
                                DeleteRefreshTokensByUserIdAdapterPort deleteRefreshTokensByUserIdAdapterPort,
                                CreateRefreshTokenAdapterPort createRefreshTokenAdapterPort) {
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
        this.deleteRefreshTokensByUserIdAdapterPort = deleteRefreshTokensByUserIdAdapterPort;
        this.createRefreshTokenAdapterPort = createRefreshTokenAdapterPort;
    }

    @Override
    public RefreshToken execute(String email) {
        User user = getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        deleteRefreshTokensByUserIdAdapterPort.deleteByUserId(user.getId());

        return createRefreshTokenAdapterPort.create(email);
    }
}

package com.douglaasph.clinic_api.domain.usecases;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.inbound.GetMyUserDataByEmailUseCasePort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetMyUserDataByEmailUseCase implements GetMyUserDataByEmailUseCasePort {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;

    public GetMyUserDataByEmailUseCase(GetUserByEmailAdapterPort getUserByEmailAdapterPort) {
        this.getUserByEmailAdapterPort = getUserByEmailAdapterPort;
    }

    @Override
    public User execute(String email) {
        return getUserByEmailAdapterPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

}

package com.douglaasph.clinic_api.adapter.outbound.security;

import com.douglaasph.clinic_api.domain.ports.outbound.CredentialsAuthenticatorAdapterPort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CredentialsAuthenticatorAdapter implements CredentialsAuthenticatorAdapterPort {
    private final AuthenticationManager authenticationManager;

    public CredentialsAuthenticatorAdapter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Email or password invalid");
        }
    }
}

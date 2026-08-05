package com.douglaasph.clinic_api.adapters.outbound.repository.security;

import com.douglaasph.clinic_api.adapter.outbound.security.CredentialsAuthenticatorAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialsAuthenticatorAdapterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    private CredentialsAuthenticatorAdapter adapter;

    private static final String EMAIL = "douglas@clinic.com";
    private static final String PASSWORD = "senha123";

    @Test
    @DisplayName("Should not throw an exception when authentication is valid")
    void case1() {
        adapter = new CredentialsAuthenticatorAdapter(authenticationManager);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        adapter.authenticate(EMAIL, PASSWORD);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("It should throw Bad Credentials when the Authentication Manager returns \"not authenticated\".")
    void case2() {
        adapter = new CredentialsAuthenticatorAdapter(authenticationManager);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThatThrownBy(() -> adapter.authenticate(EMAIL, PASSWORD))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password invalid");
    }

    @Test
    @DisplayName("Should propagate exception when AuthenticationManager throws BadCredentials")
    void case3() {
        adapter = new CredentialsAuthenticatorAdapter(authenticationManager);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> adapter.authenticate(EMAIL, PASSWORD))
                .isInstanceOf(BadCredentialsException.class);
    }
}

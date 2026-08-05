package com.douglaasph.clinic_api.adapter.outbound.security;

import com.douglaasph.clinic_api.domain.ports.outbound.GoogleTokenVerifierAdapterPort;
import com.douglaasph.clinic_api.exceptions.TokenException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleTokenVerifierAdapter implements GoogleTokenVerifierAdapterPort {
    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    public String verifyAndGetEmail(String googleToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleToken);

            if (idToken == null) {
                throw new TokenException("Google token is invalid or has expired.");
            }

            return idToken.getPayload().getEmail();
        } catch (TokenException e) {
            throw e;
        } catch (GeneralSecurityException | IOException e) {
            throw new TokenException("Error verifying Google token.");
        }
    }
}

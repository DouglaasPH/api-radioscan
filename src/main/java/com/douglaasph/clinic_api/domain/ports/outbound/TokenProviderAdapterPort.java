package com.douglaasph.clinic_api.domain.ports.outbound;

public interface TokenProviderAdapterPort {
    String generateToken(String username);
    String generateToken(String username, Integer expirationInMinutes);
    String extractUsername(String token);
    boolean isTokenValid(String token, String username);
}

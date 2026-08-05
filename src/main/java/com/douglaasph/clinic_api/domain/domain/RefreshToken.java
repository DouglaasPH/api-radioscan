package com.douglaasph.clinic_api.domain.domain;

import jakarta.persistence.*;

import java.time.Instant;

public class RefreshToken {
    private Long id;
    private String token;
    private User user;
    private Instant expiryDate;

    public RefreshToken() {}

    public RefreshToken(Long id, String token, User user, Instant expiryDate) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}

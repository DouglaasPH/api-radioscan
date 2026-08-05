package com.douglaasph.clinic_api.domain.domain;


public class Patient {
    private Long id;
    private String cpf;
    private String phone;
    private User user;

    public Patient() {}

    public Patient(Long id, String cpf, String phone, User user) {
        this.id = id;
        this.cpf = cpf;
        this.phone = phone;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package com.douglaasph.clinic_api.adapter.inbound.controller.user.request;

public class UpdateUserDataRequest {
    private String cpf;
    private String phone;

    public UpdateUserDataRequest() {}

    public UpdateUserDataRequest(String cpf, String phone) {
        this.cpf = cpf;
        this.phone = phone;
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
}

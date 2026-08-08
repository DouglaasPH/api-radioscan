package com.douglaasph.clinic_api.adapter.inbound.controller.patient.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePatientRequest {
    @NotBlank(message = "The name cannot be blank.")
    private String name;

    @NotBlank(message = "The email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "The password cannot be blank.")
    private String password;

    @NotBlank(message = "The cpf cannot be blank.")
    @Size(max = 14, min = 14, message = "CPF must contain 11 digits. Example: 000.000.000-00")
    private String cpf;

    @NotBlank(message = "The phone cannot be blank.")
    @Size(max = 15, min = 15, message = "Phone must contain 13 digits. Example: (81) 90000-0000")
    private String phone;
    
    public CreatePatientRequest() {}

    public CreatePatientRequest(String name, String email, String password, String cpf, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

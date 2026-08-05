package com.douglaasph.clinic_api.adapter.inbound.controller.employee.request;

import com.douglaasph.clinic_api.domain.domain.enums.Position;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateEmployeeRequest {
    @NotBlank(message = "The name cannot be blank.")
    private String name;

    @NotBlank(message = "The email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "The password cannot be blank.")
    private String password;

    @NotBlank(message = "The license number cannot be blank.")
    private String licenseNumber;

    @NotBlank(message = "The position cannot be blank.")
    private  Position position;
    
    public CreateEmployeeRequest() {}

    public CreateEmployeeRequest(String name, String email, String password, String licenseNumber, Position position) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.licenseNumber = licenseNumber;
        this.position = position;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}

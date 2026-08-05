package com.douglaasph.clinic_api.adapter.inbound.controller.user.response;

import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;

public class UserResponse {
    Long id;
    String name;
    private String email;
    private Roles role;
    private Employee employee;
    private Patient patient;

    public UserResponse() {}

    public UserResponse(Long id, String name, String email, Roles role, Employee employee, Patient patient) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.employee = employee;
        this.patient = patient;
    }

    public static UserResponse fromDomain(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getEmployee(), user.getPatient());
    }

    public Long getId() {
        return id;
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

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}

package com.douglaasph.clinic_api.domain.domain;

import com.douglaasph.clinic_api.domain.domain.enums.Roles;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Integer role;
    private Employee employee;
    private Patient patient;

    public User() {}

    public User(Long id, String name, String email, String password, Roles role, Employee employee, Patient patient) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        setRole(role);
        this.employee = employee;
        this.patient = patient;
    }

    public Roles getRole() {
        return Roles.valueOf(role);
    }

    public void setRole(Roles role) {
        if (role != null) {
            this.role = role.getCode();
        }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

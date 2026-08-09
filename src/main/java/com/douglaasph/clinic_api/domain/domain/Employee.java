package com.douglaasph.clinic_api.domain.domain;

import com.douglaasph.clinic_api.domain.domain.enums.Position;

public class Employee {
    private Long id;
    private String licenseNumber;
    private Integer position;
    private User user;

    public Employee() {}

    public Employee(Long id, String licenseNumber, Position position, User user) {
        this.id = id;
        this.licenseNumber = licenseNumber;
        setPosition(position);
        this.user = user;
    }

    public Position getPosition() {
        return Position.valueOf(position);
    }

    public void setPosition(Position position) {
        if (position != null) {
            this.position = position.getCode();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

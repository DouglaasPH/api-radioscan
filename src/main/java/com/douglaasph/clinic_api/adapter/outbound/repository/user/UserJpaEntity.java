package com.douglaasph.clinic_api.adapter.outbound.repository.user;

import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmployeeJpaEntity employee;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PatientJpaEntity patient;

    public UserJpaEntity () {}

    public UserJpaEntity(Long id, String name, String email, String password, Roles role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        setRole(role);
    }

    public Roles getRole() {
        return Roles.valueOf(role);
    }

    public void setRole(Roles role) {
        if (role != null) {
            this.role = role.getCode();
        }
    }

}

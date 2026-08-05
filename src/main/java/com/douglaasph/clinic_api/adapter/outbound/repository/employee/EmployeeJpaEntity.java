package com.douglaasph.clinic_api.adapter.outbound.repository.employee;

import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees")
public class EmployeeJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CRM or CRTR
    @Column(unique = true, nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private Integer position;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserJpaEntity user;

    public EmployeeJpaEntity(Long id, String licenseNumber, Position position, UserJpaEntity user) {
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
}

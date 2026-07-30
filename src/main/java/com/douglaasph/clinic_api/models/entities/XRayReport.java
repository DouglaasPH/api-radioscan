package com.douglaasph.clinic_api.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "x_ray_report")
public class XRayReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private Integer processingStatus;

    @Column(nullable = true)
    private String aiResult;

    @Column(nullable = true)
    private String finalMedicalDiagnosis;

    @Column(nullable = false)
    private boolean releasedToPatient = false;

    @JsonIgnore
    @OneToMany(mappedBy = "xRayReport")
    private List<Appointment> appointment;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}

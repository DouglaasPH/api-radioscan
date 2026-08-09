package com.douglaasph.clinic_api.adapters.outbound.repository.xrayreport;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJPaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.SaveXRayReportAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportRepository;
import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.domain.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SaveXRayReportAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private XRayReportRepository xRayReportRepository;

    private SaveXRayReportAdapter saveXRayReportAdapter;

    @BeforeEach
    void setUp() { saveXRayReportAdapter = new SaveXRayReportAdapter(xRayReportRepository); }

    private AppointmentJpaEntity persistedAppointment() {
        UserJpaEntity userEntity = entityManager.persistAndFlush(
                new UserJpaEntity(
                        null,
                        "Patient User",
                        "patient2@example.com",
                        "password123",
                        Roles.PATIENT
                )
        );
        EmployeeJpaEntity employeeEntity = entityManager.persistAndFlush(
                new EmployeeJpaEntity(
                        null,
                        "00000",
                        Position.DOCTOR,
                        userEntity
                )
        );

        UserJpaEntity patientUserEntity = entityManager.persistAndFlush(
                new UserJpaEntity(
                        null,
                        "Patient User",
                        "patient@example.com",
                        "password123",
                        Roles.PATIENT
                )
        );

        PatientJpaEntity patientEntity = entityManager.persistAndFlush(
                new PatientJpaEntity(
                        null,
                        "12345678900",
                        "+55 81 90000-0000",
                        patientUserEntity
                )
        );

        AppointmentJpaEntity entity = new AppointmentJpaEntity();
        entity.setDateHour(java.time.LocalDateTime.now().plusDays(1));
        entity.setType(AppointmentType.EXAM_CAPTURE);
        entity.setStatus(AppointmentStatus.SCHEDULED);
        entity.setEmployee(employeeEntity);
        entity.setPatient(patientEntity);

        return entityManager.persistAndFlush(entity);
    }

    @Test
    @DisplayName("Must save xrayreport")
    void case1() {
        XRayReport report = new XRayReport(
                null,
                Instant.now(),
                "exams/abc-123.png",
                ProcessingStatus.PROCESSED_BY_IA.getCode(),
                null,
                null,
                false,
                Collections.emptyList()
        );

        XRayReport result = saveXRayReportAdapter.save(report);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getS3Key()).isEqualTo("exams/abc-123.png");
        assertThat(result.getProcessingStatus()).isEqualTo(ProcessingStatus.PROCESSED_BY_IA.getCode());
        assertThat(result.isReleasedToPatient()).isFalse();
    }

    @Test
    @DisplayName("Must correctly save a report with a linked appointment.")
    void case2() {
        AppointmentJpaEntity appointmentEntity = persistedAppointment();
        Appointment appointmentDomain = AppointmentJPaMapper.toDomain(appointmentEntity);

        XRayReport report = new XRayReport(
                null,
                Instant.now(),
                "exams/with-appointment.png",
                ProcessingStatus.PROCESSED_BY_IA.getCode(),
                null,
                null,
                false,
                List.of()
        );

        XRayReport result = saveXRayReportAdapter.save(report);

        assertThat(result.getAppointment()).hasSize(0);
    }

    @Test
    @DisplayName("Must update the fields of an existing report when saving it again with the same ID.")
    void case3() {
        XRayReportJpaEntity existing = new XRayReportJpaEntity();
        existing.setCreatedAt(Instant.now());
        existing.setS3Key("exams/original.png");
        existing.setProcessingStatus(ProcessingStatus.PROCESSED_BY_IA.getCode());
        existing.setReleasedToPatient(false);
        XRayReportJpaEntity persisted = entityManager.persistAndFlush(existing);

        XRayReport toUpdate = new XRayReport(
                persisted.getId(),
                persisted.getCreatedAt(),
                persisted.getS3Key(),
                ProcessingStatus.PROCESSED_BY_IA.getCode(),
                "AI diagnosis result",
                "Final doctor diagnosis",
                true,
                Collections.emptyList()
        );

        XRayReport result = saveXRayReportAdapter.save(toUpdate);

        assertThat(result.getId()).isEqualTo(persisted.getId());
        assertThat(result.getAiResult()).isEqualTo("AI diagnosis result");
        assertThat(result.getFinalMedicalDiagnosis()).isEqualTo("Final doctor diagnosis");
        assertThat(result.isReleasedToPatient()).isTrue();

        // garante que não criou um segundo registro
        assertThat(xRayReportRepository.findAll()).hasSize(1);
    }

}

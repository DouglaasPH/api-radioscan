package com.douglaasph.clinic_api.adapters.outbound.repository.metrics;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.metrics.GetDashboardMetricsForAdminAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.xrayreport.XRayReportRepository;
import com.douglaasph.clinic_api.domain.domain.enums.*;
import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetDashboardMetricsForAdminAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private XRayReportRepository xRayReportRepository;

    private GetDashboardMetricsForAdminAdapter getDashboardMetricsForAdminAdapter;

    @BeforeEach
    void setUp() {
        getDashboardMetricsForAdminAdapter = new GetDashboardMetricsForAdminAdapter(
                patientRepository,
                appointmentRepository,
                xRayReportRepository
        );
    }

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private XRayReportJpaEntity xRayReportCreatedAt(Instant createdAt) {
        return new XRayReportJpaEntity(
                null,
                createdAt,
                "s3key",
                ProcessingStatus.PROCESSED_BY_IA.getCode(),
                null,
                null,
                false,
                null
        );
    }

    private AppointmentJpaEntity appointmentWith(
            PatientJpaEntity patient,
            EmployeeJpaEntity employee,
            Integer status,
            LocalDateTime dateHour
    ) {
        return new AppointmentJpaEntity(
                null,
                employee,
                patient,
                dateHour,
                AppointmentStatus.valueOf(status),
                AppointmentType.REPORT_REVIEW,
                null
        );
    }

    private PatientJpaEntity anyPatient(String cpf, String phone, String email) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(
                        null,
                        "Test Patient",
                        email,
                        "password123",
                        Roles.PATIENT
                )
        );

        PatientJpaEntity entity = new PatientJpaEntity();
        entity.setUser(user);
        entity.setCpf(cpf);
        entity.setPhone(phone);
        return entity;
    }

    @Test
    @DisplayName("Must get metrics")
    void case1() {
        // arrange
        LocalDate today = LocalDate.now(ZONE_ID);
        Instant nowInsideToday = today.atTime(12, 0).atZone(ZONE_ID).toInstant();
        Instant yesterday = today.minusDays(1).atTime(12, 0).atZone(ZONE_ID).toInstant();
        Instant tomorrow = today.plusDays(1).atTime(0, 0, 1).atZone(ZONE_ID).toInstant();

        entityManager.persistAndFlush(xRayReportCreatedAt(nowInsideToday));
        entityManager.persistAndFlush(xRayReportCreatedAt(yesterday));
        entityManager.persistAndFlush(xRayReportCreatedAt(tomorrow));

        // act
        DashboardAdminMetricsResult result = getDashboardMetricsForAdminAdapter.get();

        assertThat(result.getNumberOfExamsPerformed()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should only count appointments with status 1 and a dateHour in the future.")
    void case2() {
        // arrange
        UserJpaEntity userPatient = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Patient One", "patient@clinic.com", "pass123", Roles.PATIENT)
        );
        UserJpaEntity userEmployee = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Doctor One", "doc@clinic.com", "pass123", Roles.EMPLOYEE)
        );

        PatientJpaEntity patient = entityManager.persistAndFlush(
                new PatientJpaEntity(null, "12345678900", "+55 81 90000-0000", userPatient)
        );
        EmployeeJpaEntity employee = entityManager.persistAndFlush(
                new EmployeeJpaEntity(null, "LIC-12345", Position.DOCTOR, userEmployee)
        );

        // arrange
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        LocalDateTime past = LocalDateTime.now().minusDays(1);

        entityManager.persistAndFlush(appointmentWith(patient, employee, 1, future));
        entityManager.persistAndFlush(appointmentWith(patient, employee, 2, future));
        entityManager.persistAndFlush(appointmentWith(patient, employee, 1, past));

        // act
        DashboardAdminMetricsResult result = getDashboardMetricsForAdminAdapter.get();

        // assert
        assertThat(result.getAvailableAppointmentSlots()).isEqualTo(1);
    }

    @Test
    @DisplayName("Must include the total number of registered patients, without a date filter.")
    void case3() {
        // arrange
        entityManager.persistAndFlush(anyPatient("000.000.000-00", "+55 81 90000-0000", "example@gmail.com"));
        entityManager.persistAndFlush(anyPatient("000.000.000-01", "+55 81 90000-0001", "example1@gmail.com"));
        entityManager.persistAndFlush(anyPatient("000.000.000-02", "+55 81 90000-0002", "example2@gmail.com"));

        // act
        DashboardAdminMetricsResult result = getDashboardMetricsForAdminAdapter.get();

        // assert
        assertThat(result.getTotalNumberOfPatients()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return zero for all metrics when there is no data.")
    void case4() {
        setUp();

        DashboardAdminMetricsResult result = getDashboardMetricsForAdminAdapter.get();

        assertThat(result.getNumberOfExamsPerformed()).isZero();
        assertThat(result.getAvailableAppointmentSlots()).isZero();
        assertThat(result.getTotalNumberOfPatients()).isZero();
    }
}

package com.douglaasph.clinic_api.adapters.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.GetAllAvailabilitiesAppointmentsAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import com.douglaasph.clinic_api.domain.results.AvailabilitiesAppointmentsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetAllAvailabilitiesAppointmentsAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private GetAllAvailabilitiesAppointmentsAdapter getAllAvailabilitiesAppointmentsAdapter;

    @BeforeEach
    void setUp() { getAllAvailabilitiesAppointmentsAdapter = new GetAllAvailabilitiesAppointmentsAdapter(appointmentRepository); }

    private EmployeeJpaEntity persistedEmployee(String email, String licenseNumber) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Example example", email, "pass123", Roles.EMPLOYEE)
        );
        return entityManager.persistAndFlush(
                new EmployeeJpaEntity(null, licenseNumber, Position.DOCTOR, user)
        );
    }

    private PatientJpaEntity persistedPatient(String cpf, String email) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Paciente Teste", email, "pass123", Roles.PATIENT)
        );
        return entityManager.persistAndFlush(
                new PatientJpaEntity(null, cpf, "+55 81 90000-0000", user)
        );
    }

    private void persistAppointment(EmployeeJpaEntity employee, PatientJpaEntity patient, LocalDateTime dateHour, AppointmentType type, AppointmentStatus status) {
        AppointmentJpaEntity appointment = new AppointmentJpaEntity();
        appointment.setEmployee(employee);
        appointment.setPatient(patient);
        appointment.setDateHour(dateHour);
        appointment.setType(type);
        appointment.setStatus(status);
        entityManager.persistAndFlush(appointment);
    }

    @Test
    @DisplayName("Must return only the appointments available for the specified date and type.")
    void case1() {
        // arrange
        LocalDate targetDate = LocalDate.of(2026, 8, 10);

        EmployeeJpaEntity doctor = persistedEmployee("doc1@clinic.com", "LIC-100");
        PatientJpaEntity patient = persistedPatient("12345678900", "patient1@clinic.com");

        persistAppointment(doctor, patient, targetDate.atTime(10, 0), AppointmentType.REPORT_REVIEW, AppointmentStatus.AVAILABLE);
        persistAppointment(doctor, patient, targetDate.plusDays(1).atTime(10, 0), AppointmentType.REPORT_REVIEW, AppointmentStatus.AVAILABLE);
        persistAppointment(doctor, patient, targetDate.atTime(14, 0), AppointmentType.EXAM_CAPTURE, AppointmentStatus.AVAILABLE);

        // act
        Page<AvailabilitiesAppointmentsResult> result = getAllAvailabilitiesAppointmentsAdapter.get(
                targetDate,
                AppointmentType.REPORT_REVIEW,
                0
        );

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().getFirst().getAppointmentType()).isEqualTo(AppointmentType.REPORT_REVIEW.getCode());
        assertThat(result.getContent().getFirst().getDateHour()).isEqualTo(targetDate.atTime(10, 0));
    }

    @Test
    @DisplayName("Must implement pagination with a fixed limit of 4 elements.")
    void case2() {
        // arrange
        LocalDate targetDate = LocalDate.of(2026, 8, 10);
        EmployeeJpaEntity doctor = persistedEmployee("doc2@clinic.com", "LIC-200");
        PatientJpaEntity patient = persistedPatient("00011122233", "patient2@clinic.com");

        for (int i = 8; i < 13; i++) {
            persistAppointment(doctor, patient, targetDate.atTime(i, 0), AppointmentType.REPORT_REVIEW, AppointmentStatus.AVAILABLE);
        }

        // act
        Page<AvailabilitiesAppointmentsResult> page0 = getAllAvailabilitiesAppointmentsAdapter.get(
                targetDate,
                AppointmentType.REPORT_REVIEW,
                0
        );

        Page<AvailabilitiesAppointmentsResult> page1 = getAllAvailabilitiesAppointmentsAdapter.get(
                targetDate,
                AppointmentType.REPORT_REVIEW,
                1
        );

        // assert
        assertThat(page0.getTotalElements()).isEqualTo(5);
        assertThat(page0.getContent()).hasSize(4);
        assertThat(page1.getContent()).hasSize(1);
    }
}

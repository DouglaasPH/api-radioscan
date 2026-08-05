package com.douglaasph.clinic_api.adapters.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.SaveAppointmentAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaMapper;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.Appointment;
import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.Patient;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import com.douglaasph.clinic_api.domain.domain.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SaveAppointmentAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private SaveAppointmentAdapter saveAppointmentAdapter;

    @BeforeEach
    void setUp() {
        saveAppointmentAdapter = new SaveAppointmentAdapter(appointmentRepository);
    }

    private EmployeeJpaEntity persistedEmployee(String email, String licenseNumber) {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Dr. Silva", email, "pass123", Roles.EMPLOYEE)
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

    @Test
    @DisplayName("Should successfully save a new appointment and return the domain object with the generated ID.")
    void case1() {
        // arrange
        EmployeeJpaEntity doctorEntity = persistedEmployee("doctor@clinic.com", "CRM-12345");
        PatientJpaEntity patientEntity = persistedPatient("12345678900", "patient@clinic.com");

        Employee doctorDomain = EmployeeJpaMapper.toDomain(doctorEntity);
        Patient patientDomain = PatientJpaMapper.toDomain(patientEntity);

        LocalDateTime dateHour = LocalDateTime.of(2026, 8, 10, 14, 30);
        Appointment appointmentToSave = new Appointment(
                null,
                doctorDomain,
                patientDomain,
                dateHour,
                AppointmentStatus.AVAILABLE,
                AppointmentType.REPORT_REVIEW,
                null
        );

        // act
        Appointment savedAppointment = saveAppointmentAdapter.save(appointmentToSave);

        // assert
        assertThat(savedAppointment).isNotNull();
        assertThat(savedAppointment.getId()).isNotNull();
        assertThat(savedAppointment.getDateHour()).isEqualTo(dateHour);
        assertThat(savedAppointment.getType()).isEqualTo(AppointmentType.REPORT_REVIEW);
        assertThat(savedAppointment.getStatus()).isEqualTo(AppointmentStatus.AVAILABLE);

        assertThat(savedAppointment.getEmployee()).isNotNull();
        assertThat(savedAppointment.getEmployee().getId()).isEqualTo(doctorEntity.getId());
        assertThat(savedAppointment.getPatient()).isNotNull();
        assertThat(savedAppointment.getPatient().getId()).isEqualTo(patientEntity.getId());
    }
}

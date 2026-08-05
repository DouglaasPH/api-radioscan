package com.douglaasph.clinic_api.adapters.outbound.repository.appointment;

import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.AppointmentRepository;
import com.douglaasph.clinic_api.adapter.outbound.repository.appointment.GetAppointmentByIdAdapter;
import com.douglaasph.clinic_api.adapter.outbound.repository.employee.EmployeeJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.patient.PatientJpaEntity;
import com.douglaasph.clinic_api.adapter.outbound.repository.user.UserJpaEntity;
import com.douglaasph.clinic_api.domain.domain.Appointment;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GetAppointmentByIdAdapterTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private GetAppointmentByIdAdapter getAppointmentByIdAdapter;

    @BeforeEach
    void setUp() { getAppointmentByIdAdapter = new GetAppointmentByIdAdapter(appointmentRepository); }

    private EmployeeJpaEntity persistedEmployee() {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Example example", "doc1@clinic.com", "pass123", Roles.EMPLOYEE)
        );
        return entityManager.persistAndFlush(
                new EmployeeJpaEntity(null, "LIC-100", Position.DOCTOR, user)
        );
    }

    private PatientJpaEntity persistedPatient() {
        UserJpaEntity user = entityManager.persistAndFlush(
                new UserJpaEntity(null, "Paciente Teste", "patient1@clinic.com", "pass123", Roles.PATIENT)
        );
        return entityManager.persistAndFlush(
                new PatientJpaEntity(null, "12345678900", "+55 81 90000-0000", user)
        );
    }

    private AppointmentJpaEntity persistAppointment(EmployeeJpaEntity employee, PatientJpaEntity patient, LocalDateTime dateHour, AppointmentType type, AppointmentStatus status) {
        AppointmentJpaEntity appointment = new AppointmentJpaEntity();
        appointment.setEmployee(employee);
        appointment.setPatient(patient);
        appointment.setDateHour(dateHour);
        appointment.setType(type);
        appointment.setStatus(status);
        return entityManager.persistAndFlush(appointment);
    }


    @Test
    @DisplayName("Must find appointment by id")
    void case1() {
        // arrange
        LocalDate targetDate = LocalDate.of(2026, 8, 10);

        EmployeeJpaEntity doctor = persistedEmployee();
        PatientJpaEntity patient = persistedPatient();

        AppointmentJpaEntity savedAppointment = persistAppointment(doctor, patient, targetDate.atTime(10, 0), AppointmentType.REPORT_REVIEW, AppointmentStatus.AVAILABLE);

        // act
        Optional<Appointment> result = getAppointmentByIdAdapter.get(savedAppointment.getId());

        // assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedAppointment.getId());
    }

    @Test
    @DisplayName("Should return an empty Optional when the id does not exists")
    void case2() {
        // act
        Optional<Appointment> result = getAppointmentByIdAdapter.get(1L);

        // assert
        assertThat(result).isEmpty();
    }
}
